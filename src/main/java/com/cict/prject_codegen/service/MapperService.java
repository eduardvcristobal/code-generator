package com.cict.prject_codegen.service;

import com.cict.prject_codegen.dto.AuthorDTO;
import com.cict.prject_codegen.model.Author;

import java.util.ArrayList;
import java.util.List;

//E = Entity
//D = DTO
public interface MapperService<E, D> {

    D asDTO(E entity, Class<D> dtoClass);

    E asEntity(D dto, Class<E> entityClass);

    List<E> asEntityList(List<D> dtoList, Class<E> entityClass);

    List<D> asDTOList(List<E> entityList, Class<D> dtoClass);

}