/**
 * 
 */
package com.zhixiangli.gomoku.console.common;

import java.awt.Point;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author zhixiangli
 *
 */
public enum ConsoleCommand {

    CLEAR("CLEAR"),

    NEXT_BLACK("NEXT_BLACK"),

    NEXT_WHITE("NEXT_WHITE"),

    RESET("RESET"),

    PUT("PUT"),

    PLAY_BLACK("PLAY_BLACK"),

    PLAY_WHITE("PLAY_WHITE"),

    ;

    private String text;

    private ConsoleCommand(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static final String format(ConsoleCommand command) {
        return command.getText() + StringUtils.LF;
    }

    public static final String format(ConsoleCommand command, Point point) {
        return String.format("%s %d %d", command.getText(), point.x, point.y) + StringUtils.LF;
    }

    public static final Pair<ConsoleCommand, Point> parse(String str) {
        for (ConsoleCommand command : ConsoleCommand.values()) {
            if (StringUtils.startsWith(str, command.getText())) {
                switch (command) {
                case RESET:
                case PUT:
                case PLAY_BLACK:
                case PLAY_WHITE:
                    String[] tokens = StringUtils.split(str);
                    if (ArrayUtils.getLength(tokens) == 3 && NumberUtils.isDigits(tokens[1])
                            && NumberUtils.isDigits(tokens[2])) {
                        Point point = new Point(Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2]));
                        return ImmutablePair.of(command, point);
                    }
                    return null;
                default:
                    return ImmutablePair.of(command, null);
                }
            }
        }
        return ImmutablePair.of(null, null);
    }

}
