package com.cict.prject_codegen.dto;

import com.cict.prject_codegen.base.Mergeable;
import com.cict.prject_codegen.model.Author;
import com.cict.prject_codegen.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

// @MappedSuperclass, JPA will not create a table for that class in the database, but the properties defined in that class will be inherited by its subclasses, and will be mapped to the columns in the database table of those subclasses.
@MappedSuperclass
@Getter
@Setter
public abstract class BaseDTO<E> implements Mergeable<E>  {
    private Long id;
    private String sbuId;

}
