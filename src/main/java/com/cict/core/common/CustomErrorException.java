package com.cict.core.common;

import com.cict.core.exception.ErrorKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomErrorException   {

    private String path;

    public CustomErrorException(ErrorKey errorKey, HttpStatus status) {
        super();
        this.error = status.toString();
        this.message = errorKey.getMessage();
        this.status = status.value();
    }

    private String error;
    private String message;
    private Integer status;

    public static CustomErrorException of (ErrorKey errorKey, HttpStatus status) {
        CustomErrorException customErrorException = new CustomErrorException(errorKey, status);

        return customErrorException;
    }


}
