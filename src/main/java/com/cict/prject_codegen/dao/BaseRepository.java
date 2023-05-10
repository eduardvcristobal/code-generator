package com.cict.prject_codegen.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
//EN = Entity
//P = Primary Key
public interface BaseRepository<E, P> extends JpaRepository<E, Long>, JpaSpecificationExecutor<E> {
}
