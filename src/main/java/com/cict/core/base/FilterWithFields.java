package com.cict.core.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterWithFields {
    private String[] fields;
    private List<Filter> filters;
    // getters and setters
}
