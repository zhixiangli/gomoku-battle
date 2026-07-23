package com.zhixiangli.gomoku.console;

import com.zhixiangli.gomoku.console.common.ConsoleProcess;
import com.zhixiangli.gomoku.core.service.ChessboardService;
import org.junit.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConsoleMasterTest {

    @Test
    public void closeStopsAgentsAndDetachesTheGameListener() throws Exception {
        final ChessboardService chessboardService = ChessboardService.getInstance();
        final CountDownLatch processCreated = new CountDownLatch(1);
        final AtomicInteger processCount = new AtomicInteger();
        final AtomicReference<ConsoleProcess> processReference = new AtomicReference<>();
        final String configPath = getClass().getClassLoader().getResource("ai_player.properties").getPath();

        final ConsoleMaster master = new ConsoleMaster(configPath, chessboardService, command -> {
            processCount.incrementAndGet();
            final ConsoleProcess process = new ConsoleProcess(List.of("sleep", "10"), Duration.ofSeconds(1));
            processReference.set(process);
            processCreated.countDown();
            return process;
        });
        try {
            chessboardService.restart();
            assertTrue("Expected the black agent to be started", processCreated.await(1, TimeUnit.SECONDS));

            final ConsoleProcess process = processReference.get();
            assertTrue(process.isAlive());

            master.close();
            assertFalse(process.isAlive());

            chessboardService.restart();
            assertEquals(1, processCount.get());
        } finally {
            master.close();
        }
    }

}
