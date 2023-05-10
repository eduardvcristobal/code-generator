package com.cict.prject_codegen.base;

import com.cict.prject_codegen.model.BaseEntity;

import java.util.Collection;
import java.util.function.BiConsumer;

public class EntityRelater {

    /**
     * Helper method that is used to assign the parent entity to each of the the child entities
     * in the set.
     */
    public static <P extends BaseEntity, C extends BaseEntity> void relate(P parent, Collection<C> children,
                                                                         BiConsumer<C, P> setter) {
        if (children != null) {
            children.forEach(child -> setter.accept(child, parent));
        }
    }

    public static <P extends BaseEntity, C extends BaseEntity> void relate(P parent, C children,
                                                                           BiConsumer<C, P> setter) {
        if (children != null) {
             setter.accept(children, parent);
        }
    }
}
