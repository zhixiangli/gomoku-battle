package com.zhixiangli.gomoku.console.common;

import com.zhixiangli.gomoku.core.common.GomokuConst;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConsoleProtocolTest {

    @Test
    public void parsesAValidRequest() {
        final ConsoleRequest request = ConsoleProtocol.parseRequest(
                "{\"command\":\"NEXT_BLACK\",\"rows\":15,\"columns\":15,\"chessboard\":\"\"}");

        assertEquals(ConsoleCommand.NEXT_BLACK, request.getCommand());
    }

    @Test
    public void rejectsInvalidRequests() {
        assertInvalidRequest("{\"command\":\"NEXT_GREEN\",\"rows\":15,\"columns\":15,\"chessboard\":\"\"}");
        assertInvalidRequest("{\"command\":\"NEXT_BLACK\",\"rows\":14,\"columns\":15,\"chessboard\":\"\"}");
        assertInvalidRequest("{\"command\":\"NEXT_BLACK\",\"rows\":15,\"columns\":15,\"chessboard\":\"B[7f]\"}");
        assertInvalidRequest("{\"command\":\"NEXT_BLACK\",\"rows\":15,\"columns\":15,\"chessboard\":\"B[77];B[78]\"}");
        assertInvalidRequest("[]");
    }

    @Test
    public void rejectsInvalidResponses() {
        assertInvalidResponse("{\"rowIndex\":15,\"columnIndex\":0}");
        assertInvalidResponse("[]");
    }

    private void assertInvalidRequest(final String json) {
        try {
            ConsoleProtocol.parseRequest(json);
            fail("Expected invalid request to be rejected: " + json);
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    private void assertInvalidResponse(final String json) {
        try {
            ConsoleProtocol.parseResponse(json);
            fail("Expected invalid response to be rejected: " + json);
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

}
