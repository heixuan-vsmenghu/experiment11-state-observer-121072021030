package com.lzl.experiment11.state;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OperationLog {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String operation;
    private final String beforeState;
    private final String afterState;
    private final String message;
    private final LocalDateTime time;

    public OperationLog(String operation, String beforeState, String afterState, String message) {
        this.operation = operation;
        this.beforeState = beforeState;
        this.afterState = afterState;
        this.message = message;
        this.time = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s：%s -> %s，%s",
                time.format(FORMATTER), operation, beforeState, afterState, message);
    }
}
