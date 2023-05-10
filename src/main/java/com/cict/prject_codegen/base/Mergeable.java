package com.cict.prject_codegen.base;
import org.modelmapper.ModelMapper;

public interface Mergeable<E> {


    default E to(E entity, ModelMapper modelMapper) {
        return entity;
    }

    default E to(E entity, ModelMapper modelMapper, E... otherEntities) {
        return entity;
    }

    /*default Mergeable<? super A> from(A entity) {
        return this;
    }*/
}