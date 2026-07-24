package com.zhixiangli.gomoku.console.common;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.common.GomokuFormatter;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.util.List;

public final class ConsoleProtocol {

    private static final Gson GSON = new Gson();

    private ConsoleProtocol() {
    }

    public static ConsoleRequest parseRequest(final String json) {
        final JsonObject object = parseObject(json, "request");
        final ConsoleRequest request = GSON.fromJson(object, ConsoleRequest.class);
        if ((request == null) || (request.getCommand() == null)) {
            throw new IllegalArgumentException("Request command is required");
        }
        if ((request.getRows() != GomokuConst.CHESSBOARD_SIZE) || (request.getColumns() != GomokuConst.CHESSBOARD_SIZE)) {
            throw new IllegalArgumentException("Only " + GomokuConst.CHESSBOARD_SIZE + "x" + GomokuConst.CHESSBOARD_SIZE
                    + " boards are supported");
        }
        if (request.getChessboard() == null) {
            throw new IllegalArgumentException("Request chessboard is required");
        }
        validateMoveOrder(GomokuFormatter.toHistory(request.getChessboard()));
        return request;
    }

    public static ConsoleResponse parseResponse(final String json) {
        final JsonObject object = parseObject(json, "response");
        final int row = parseCoordinate(object, "rowIndex");
        final int column = parseCoordinate(object, "columnIndex");
        return new ConsoleResponse(row, column);
    }

    private static JsonObject parseObject(final String json, final String messageType) {
        if (json == null) {
            throw new IllegalArgumentException("Agent " + messageType + " cannot be null");
        }
        try {
            final JsonElement element = JsonParser.parseString(json);
            if (!element.isJsonObject()) {
                throw new IllegalArgumentException("Agent " + messageType + " must be a JSON object");
            }
            return element.getAsJsonObject();
        } catch (final RuntimeException e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException("Invalid agent " + messageType + " JSON", e);
        }
    }

    private static int parseCoordinate(final JsonObject object, final String name) {
        final JsonElement value = object.get(name);
        if ((value == null) || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException("Agent response requires numeric " + name);
        }
        try {
            final int coordinate = value.getAsBigDecimal().intValueExact();
            if ((coordinate < 0) || (coordinate >= GomokuConst.CHESSBOARD_SIZE)) {
                throw new IllegalArgumentException("Agent response " + name + " is out of range");
            }
            return coordinate;
        } catch (final NumberFormatException | ArithmeticException e) {
            throw new IllegalArgumentException("Agent response " + name + " must be an integer", e);
        }
    }

    private static void validateMoveOrder(final List<Pair<ChessType, Point>> history) {
        ChessType expectedChessType = ChessType.BLACK;
        for (final Pair<ChessType, Point> move : history) {
            if (move.getLeft() != expectedChessType) {
                throw new IllegalArgumentException("Invalid SGF move order at " + move.getRight());
            }
            expectedChessType = (expectedChessType == ChessType.BLACK) ? ChessType.WHITE : ChessType.BLACK;
        }
    }

}
