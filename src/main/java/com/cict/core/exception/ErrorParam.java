package com.cict.core.exception;

/**
 * Contains the fields used by the errors in {@link ErrorKey}.
 */
public enum ErrorParam {
    EXPECTED("expected"),
    ACTUAL("actual"),
    STATUS("status");

    private String name;

    ErrorParam(String name) {
        this.name = name;
    }

    /**
     * Returns the field name;
     */
    public String getName() {
        return name;
    }
}
