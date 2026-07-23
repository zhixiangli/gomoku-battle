package com.zhixiangli.gomoku.console;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ConsoleBootstrapTest {

    @Test
    public void commandLineRequiresPlayerConfiguration() {
        try {
            new DefaultParser().parse(ConsoleBootstrap.createOptions(), new String[0]);
            fail("Expected the player configuration option to be required");
        } catch (final ParseException expected) {
            // expected
        }
    }

}
