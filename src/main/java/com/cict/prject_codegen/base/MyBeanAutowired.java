package com.cict.prject_codegen.base;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.persistence.EntityManager;

public abstract class MyBeanAutowired {

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    @Qualifier("entityManager")
    protected EntityManager entityManager;

}
