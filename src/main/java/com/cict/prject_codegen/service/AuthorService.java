package com.cict.prject_codegen.service;

import com.cict.prject_codegen.dto.AuthorDTO;
import com.cict.prject_codegen.model.Author;
import com.cict.prject_codegen.model.Book;

public interface AuthorService extends GenericService<Author, AuthorDTO, Long> {

//    Author addBook(Long authorId, Book book);
    Object saveAuthorWithBooks(AuthorDTO authorDto);
}