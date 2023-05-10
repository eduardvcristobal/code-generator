package com.cict.prject_codegen.base;

import com.cict.prject_codegen.base.MyBeanAutowired;

import java.util.ArrayList;
import java.util.List;

public abstract class MyMapper<E,D> extends MyBeanAutowired {

    public Object asDTOObject(E entity, Class<?> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    public D asDTO(E entity, Class<D> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    public E asEntity(D dto, Class<E> entityClass) {
        return modelMapper.map(dto, entityClass);
    }

    public List<E> asEntityList(List<D> dtoList, Class<E> entityClass) {
        if (dtoList == null) {
            return null;
        }

        List<E> entityList = new ArrayList<E>();
        for (D dto : dtoList) {
            entityList.add(modelMapper.map(dto, entityClass));
        }

        return entityList;
    }

    public List<D> asDTOList(List<E> entityList, Class<D> dtoClass) {
        if (entityList == null) {
            return null;
        }

        List<D> dtoList = new ArrayList<D>();
        for (E entity : entityList) {
            dtoList.add(modelMapper.map(entity, dtoClass));
        }

        return dtoList;
    }

}
