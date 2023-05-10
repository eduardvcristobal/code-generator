package com.cict.core.base;

import com.cict.core.util.LocalDateTimeDeserializer;
import com.cict.core.util.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseDTO<T extends BaseModel> extends BaseMergable<T> {

    private String sbuId;
    private String createdBy;
    private String updatedBy;
    private Boolean active;

    @Transient
    private transient String code;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeCreated;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeUpdated;

    @Override
    public String toString() {
        return "{\"BaseDTO\":{" + "\"sbuId\":\"" + sbuId + "\""
                + ",\"createdBy\":\"" + createdBy + "\""
                + ",\"updatedBy\":\"" + updatedBy + "\""
                + ",\"active\":\"" + active + "\""
                + ",\"code\":\"" + code + "\""
                + "}}";
    }
}
