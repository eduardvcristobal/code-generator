package com.cict.core.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ForgotPasswordRequest implements Serializable {

    String username;

    String email;

    String body;

    private String latLong;

    private String userAgent;

    private String ipAddress;
}
