package com.cict.core.base;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseMergable<T extends BaseModel> implements Mergeable<T> {

    /**
     * Id place holder for Base Mergable object
     */
    private String id;

    /**
     * placeholder for the actual numeric data ID of the entity
     */
    private Long documentId = null;

}