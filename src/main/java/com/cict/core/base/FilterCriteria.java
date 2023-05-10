package com.cict.core.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterCriteria {
    private String field;
    private String[] fields;
    private QueryOperator operator;
    private Object value;
}
