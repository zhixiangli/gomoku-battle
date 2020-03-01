/**
 *
 */
package com.zhixiangli.gomoku.console.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author zhixiangli
 *
 */
public class ConsoleProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleProcess.class);

    private final BufferedReader reader;

    private final BufferedWriter writer;

    public ConsoleProcess(final String command) throws IOException {
        final ProcessBuilder builder = new ProcessBuilder(StringUtils.split(command));
        final Process process = builder.start();
        final OutputStream stdin = process.getOutputStream();
        final InputStream stdout = process.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stdout));
        writer = new BufferedWriter(new OutputStreamWriter(stdin));
    }

    public void send(final String message) throws IOException {
        LOGGER.info("send message start: {}", message);
        writer.write(message);
        writer.flush();
        LOGGER.info("sent message finish: {}", message);
    }

    public String receive() throws IOException {
        LOGGER.info("receive message start");
        String line;
        while ((line = reader.readLine()) == null) {
        }
        LOGGER.info("receive message finish: {}", line);
        return line;
    }

}
