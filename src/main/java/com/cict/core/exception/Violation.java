package com.cict.core.exception;

import lombok.Getter;

@Getter
public class Violation {
    private final String field;
    private final Object value;
    private final String message;

    public Violation(String field, String message) {
        this.field = field;
        this.message = message;
        this.value = null;
    }

    public Violation(String field, String message, Object value) {
        this.field = field;
        this.message = message;
        this.value = value;
    }
}