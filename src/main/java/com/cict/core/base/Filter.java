package com.cict.core.base;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Filter {
    private String module;
    private String field;
    private QueryOperator operator;
    private String value;
    private List<String> values;//Used in case of IN operator
    private List<Integer> valuesInt;
    private LocalDate dateFrom;
    private LocalDate dateTo;


    public Filter( String field, QueryOperator operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }
}