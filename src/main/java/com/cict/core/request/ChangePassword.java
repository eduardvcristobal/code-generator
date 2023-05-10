package com.cict.core.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePassword {

    @Override
    public String toString() {
        return "ChangePassword{" +
                "newPassword='" + newPassword + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }

    String newPassword;

    String confirmPassword;

    private String userAgent;

    private String ipAddress;

    private String latLong;
}
