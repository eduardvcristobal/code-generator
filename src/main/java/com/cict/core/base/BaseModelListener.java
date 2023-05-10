package com.cict.core.base;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Handles and determines the default value that should be assigned to the entity.
 */
public class BaseModelListener {

    /**
     * Sets the entity fields before the persist process.
     */
   /* @PrePersist
    public void prePersist(Object entity) {
        setDefault(entity);
    }

    *//**
     * Sets the entity fields before the update process.
     *//*
    @PreUpdate
    public void preUpdate(Object entity) {
        setDefault(entity);
    }

    private void setDefault(Object entity) {

        if ( ((BaseModel)entity).getActive() == null ) {
            ((BaseModel)entity).setActive(true);
        }
    }*/
}