package com.cict.prject_codegen.service;

import com.cict.core.base.FilterRequest;
import com.cict.prject_codegen.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface GenericService<ENTITY, DTO, ID> extends MapperService<ENTITY, DTO> {

    ENTITY save(ENTITY entity);
    
    List<ENTITY> save(List<ENTITY> entities);

    List<DTO> saveAll(List<DTO> dtoList, Class<ENTITY> entityClass, Class<DTO> dtoClass);

//    List<DTO> saveAll(List<DTO> dtoList);

    void deleteById(ID id);

    Optional<ENTITY> findById(ID id);

    List<ENTITY> findAll();

    Page<ENTITY> findAll(Pageable pageable);

    Object update(DTO dto, Long id, Class<?> dtoClass);

    List<ENTITY> findAllSpecsAsList(FilterRequest filter);

    Page<ENTITY> findAllSpecsAsPage(FilterRequest filter, Pageable pageable);
    
    List<?> findAllSpecsAsListWithFields(FilterRequest filter, Class<ENTITY> entity);

    boolean existsById(Long id);

    DTO saveDTO(DTO dto, Class<ENTITY> entityClass, Class<DTO> dtoClass);

    ENTITY createOne(long sbuId, DTO resource);

}