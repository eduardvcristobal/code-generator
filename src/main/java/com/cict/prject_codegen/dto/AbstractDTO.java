package com.cict.prject_codegen.dto;

//Use this class if you want to change the type of the id field
public class AbstractDTO<E> {

    private E id;

    public E getId() {
        return id;
    }

    public void setId(E id) {
        this.id = id;
    }
}