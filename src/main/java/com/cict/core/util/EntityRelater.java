package com.cict.core.util;

import com.cict.core.base.BaseModel;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * Handles the process of updating entity relationships.
 */
public class EntityRelater {

    /**
     * Helper method that is used to assign the parent entity to each of the the child entities
     * in the set.
     */
    public static <T extends BaseModel, P extends BaseModel> void relate(P parent, Collection<T> children,
                                                                         BiConsumer<T, P> setter) {
        if (children != null) {
            children.forEach(t -> {
                setter.accept(t, parent);
//                t.setSbuId( parent.getSbuId() );
            });
        }
    }

    /**
     * Helper method that is used to assign the parent entity to child entity.
     */
    public static <T extends BaseModel, P extends BaseModel> void relate(P parent, T child,
        BiConsumer<T, P> setter) {
        if (child != null) {
            setter.accept(child, parent);
//            child.setSbuId(parent.getSbuId());
        }
    }

}
