package com.cict.prject_codegen.dao;

import com.cict.prject_codegen.model.Book;
import org.springframework.stereotype.Repository;

@Repository()
public interface BookRepository extends BaseRepository<Book, Long> {

}