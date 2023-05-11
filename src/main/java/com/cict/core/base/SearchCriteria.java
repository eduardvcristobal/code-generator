package com.cict.core.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCriteria {
    private String key;
    private SearchOperation operation;
    private Object value;

    public SearchCriteria(String key, SearchOperation operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    // Getters and setters for the fields
    // ...
}
