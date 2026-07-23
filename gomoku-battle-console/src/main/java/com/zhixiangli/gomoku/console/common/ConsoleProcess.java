package com.zhixiangli.gomoku.console.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Line-oriented stdio connection to one external agent process.
 */
public class ConsoleProcess implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleProcess.class);

    private static final Duration DEFAULT_RESPONSE_TIMEOUT = Duration.ofMinutes(2);

    private static final Duration CLOSE_TIMEOUT = Duration.ofSeconds(2);

    private final Process process;

    private final BufferedReader reader;

    private final BufferedWriter writer;

    private final ExecutorService stdoutReader;

    private final Thread stderrDrainer;

    private final Duration responseTimeout;

    public ConsoleProcess(final String command) throws IOException {
        this(List.of("sh", "-c", "exec " + requireCommand(command)), DEFAULT_RESPONSE_TIMEOUT);
    }

    public ConsoleProcess(final List<String> command, final Duration responseTimeout) throws IOException {
        if ((command == null) || command.isEmpty()) {
            throw new IllegalArgumentException("Agent command is required");
        }
        if ((responseTimeout == null) || responseTimeout.isNegative() || responseTimeout.isZero()) {
            throw new IllegalArgumentException("Agent response timeout must be positive");
        }

        this.responseTimeout = responseTimeout;
        process = new ProcessBuilder(command).start();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
        stdoutReader = Executors.newSingleThreadExecutor(newDaemonThreadFactory("gomoku-agent-stdout"));
        stderrDrainer = newDaemonThreadFactory("gomoku-agent-stderr").newThread(this::drainStderr);
        stderrDrainer.start();
    }

    public synchronized void send(final String message) throws IOException {
        if (!process.isAlive()) {
            throw exitedProcessException();
        }
        LOGGER.info("send message start: {}", message);
        writer.write(message);
        writer.flush();
        LOGGER.info("sent message finish: {}", message);
    }

    public synchronized String receive() throws IOException {
        LOGGER.info("receive message start");
        final Future<String> lineFuture = stdoutReader.submit(reader::readLine);
        try {
            final String line = lineFuture.get(responseTimeout.toMillis(), TimeUnit.MILLISECONDS);
            if (line == null) {
                throw exitedProcessException();
            }
            LOGGER.info("receive message finish: {}", line);
            return line;
        } catch (final TimeoutException e) {
            lineFuture.cancel(true);
            close();
            throw new IOException("Agent did not respond within " + responseTimeout, e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for agent response", e);
        } catch (final ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new IOException("Unable to read agent response", cause);
        }
    }

    @Override
    public synchronized void close() {
        closeQuietly(writer);
        if (process.isAlive()) {
            process.destroy();
            try {
                if (!process.waitFor(CLOSE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)) {
                    process.destroyForcibly();
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                process.destroyForcibly();
            }
        }
        closeQuietly(reader);
        stdoutReader.shutdownNow();
    }

    private void drainStderr() {
        try (BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = stderr.readLine()) != null) {
                LOGGER.trace("agent stderr: {}", line);
            }
        } catch (final IOException e) {
            if (process.isAlive()) {
                LOGGER.warn("Unable to read agent stderr", e);
            }
        }
    }

    private IOException exitedProcessException() {
        final String exitStatus = process.isAlive() ? "unknown" : String.valueOf(process.exitValue());
        return new IOException("Agent exited without a response (exit status " + exitStatus + ")");
    }

    private static String requireCommand(final String command) {
        if (StringUtils.isBlank(command)) {
            throw new IllegalArgumentException("Agent command is required");
        }
        return command;
    }

    private static ThreadFactory newDaemonThreadFactory(final String name) {
        return runnable -> {
            final Thread thread = new Thread(runnable, name);
            thread.setDaemon(true);
            return thread;
        };
    }

    private static void closeQuietly(final AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (final Exception e) {
            LOGGER.debug("Unable to close agent stream", e);
        }
    }

}
