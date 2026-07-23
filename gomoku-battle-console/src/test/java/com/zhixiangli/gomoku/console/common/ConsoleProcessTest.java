package com.zhixiangli.gomoku.console.common;

import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConsoleProcessTest {

    @Test
    public void receiveRejectsAgentEof() throws Exception {
        try (ConsoleProcess process = new ConsoleProcess(List.of("sh", "-c", "exit 0"), Duration.ofSeconds(1))) {
            assertReceiveFails(process, "exited without a response");
        }
    }

    @Test
    public void receiveTimesOutForUnresponsiveAgent() throws Exception {
        try (ConsoleProcess process = new ConsoleProcess(List.of("sleep", "5"), Duration.ofMillis(100))) {
            assertReceiveFails(process, "did not respond within");
        }
    }

    @Test
    public void stderrIsDrainedWhileWaitingForResponse() throws Exception {
        final String command = "i=0; while [ $i -lt 1000 ]; do echo diagnostic >&2; i=$((i + 1)); done; echo response";
        try (ConsoleProcess process = new ConsoleProcess(List.of("sh", "-c", command), Duration.ofSeconds(1))) {
            assertEquals("response", process.receive());
        }
    }

    private void assertReceiveFails(final ConsoleProcess process, final String expectedMessage) throws Exception {
        try {
            process.receive();
            fail("Expected agent receive to fail");
        } catch (final IOException expected) {
            org.junit.Assert.assertTrue(expected.getMessage().contains(expectedMessage));
        }
    }

}
