package com.cict.core.response;

import org.springframework.beans.factory.annotation.Value;

public interface CodeValueDTO {
    @Value("#{target.code}")
    String getCode();

    @Value("#{target.value}")
    String getValue();
}
