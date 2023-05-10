package com.cict.prject_codegen.dao;

import com.cict.prject_codegen.model.Author;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository()
public interface AuthorRepository extends BaseRepository<Author, Long> {
}
