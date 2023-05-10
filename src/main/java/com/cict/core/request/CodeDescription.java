package com.cict.core.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CodeDescription {
    private Long id;
    private String code;
    private String description;
    private String message;
    private Integer status;
    private String error;

    public CodeDescription(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
    }

    public CodeDescription(String error, String message, HttpStatus status) {
        this.error = error;
        this.message = message;
        this.status = status.value();
    }


    public CodeDescription() {
    }
}
