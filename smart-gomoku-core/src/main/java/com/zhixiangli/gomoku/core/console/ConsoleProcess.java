/**
 * 
 */
package com.zhixiangli.gomoku.core.console;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhixiangli
 *
 */
public class ConsoleProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleProcess.class);

    private BufferedReader reader;

    private BufferedWriter writer;

    private Process process;

    public ConsoleProcess(String command) throws IOException {
        if (StringUtils.isBlank(command)) {
            return;
        }

        ProcessBuilder builder = new ProcessBuilder(StringUtils.split(command));
        process = builder.start();
        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(stdout));
        this.writer = new BufferedWriter(new OutputStreamWriter(stdin));
    }

    public void send(String message) throws IOException {
        LOGGER.info("send message start: {}", message);
        this.writer.write(message);
        this.writer.flush();
        LOGGER.info("sent message finish: {}", message);
    }

    public String receive() throws IOException {
        LOGGER.info("receive message start");
        String line = null;
        while ((line = this.reader.readLine()) == null) {
        }
        LOGGER.info("receive message finish: {}", line);
        return line;
    }

}
