package com.cict.core.common;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass //its attributes will be mapped to the tables of its subclasses.
//any entity class that extends AbstractEntity will inherit the id attributes and can use them to map their respective database columns.
public abstract class AbstractEntity<Long extends Serializable> {

    private Long sbuId;



//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

//    public AbstractEntity() {
//        // empty constructor
//    }

    /*public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }*/
}
