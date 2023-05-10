package com.cict.core.base;

import org.modelmapper.ModelMapper;

import static com.cict.core.base.MergeFunctions.mergeBase;

/**
 * base interface of resources that need to merge to and from entities.
 */
public interface Mergeable<A> {

    /**
     * Returns the {@link Sameness} between two objects. Depending on the result of
     * this method, the other object may allow merging or not. If the result of this method is
     * {@link Sameness#DIFFERENT}, the other object should not undergo a merge, rather
     * it should be replaced or discarded. If the result of this method is {@link Sameness#MERGEABLE},
     * the other object can be merged using {@link Mergeable#to(Object, EntityResolver)}. If
     * the result {@link Sameness#EQUAL}, no action should occur with the other object.
     */
    default Sameness sameness(A entity) {
        return Sameness.DIFFERENT;
    }

    /**
     * Merges this resource into the entity. This should occur after validation,
     * so we make the assumption that it is safe. When merging, only updated properties
     * should be assigned to the entity to ensure they don't get dirty.
     */
    default A to(A entity, EntityResolver resolver) {
        return entity;
    }

    default A to(A entity, EntityResolver resolver, ModelMapper modelMapper) {
        mergeBase(this, entity, modelMapper);
        return entity;
    }

    /**
     * default native/primary propertie to entity using model mapper
     * @param entity
     * @param modelMapper
     * @return
     */
    default A to(A entity, ModelMapper modelMapper) {
        mergeBase(this, entity, modelMapper);
        return entity;
    }

    /**
     * Merges from the entity to this resource.
     */
    default Mergeable<? super A> from(A entity) {
        return this;
    }

    default Mergeable<? super A> from(A entity, Class<?> dtoClass, EntityResolver resolver) {
        return from(entity, dtoClass);
    }

    default Mergeable<? super A> from(A entity, Class<?> dtoClass) {
        return from(entity);
    }

    default Mergeable<? super A> from(A entity, Class<?> dtoClass, EntityResolver resolver, ModelMapper modelMapper) {
        return from(entity, dtoClass, resolver);
    }

}