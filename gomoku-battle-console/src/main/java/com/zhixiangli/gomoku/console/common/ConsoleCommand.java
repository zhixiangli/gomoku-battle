package com.zhixiangli.gomoku.console.common;

/**
 * @author zhixiangli
 */
public enum ConsoleCommand {

    NEXT_BLACK("NEXT_BLACK"),

    NEXT_WHITE("NEXT_WHITE"),

    ;

    private final String text;

    ConsoleCommand(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
