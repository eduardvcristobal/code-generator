package com.cict.prject_codegen.service.impl;

import com.cict.prject_codegen.dao.BookRepository;
import com.cict.prject_codegen.dto.BookDTO;
import com.cict.prject_codegen.model.Book;
import com.cict.prject_codegen.service.AuthorService;
import com.cict.prject_codegen.service.BookService;
import com.cict.prject_codegen.base.MySpecification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
//extends before implements. implements Service, BookService, GenericMapper
public class BookServiceImpl extends MySpecification<Book, BookDTO, BookRepository> implements BookService {
    private final BookRepository repository;
    private final AuthorService authorService;

    public BookServiceImpl(BookRepository repository,
                           AuthorService authorService
    ) {
        super(repository, Book.class, BookDTO.class);
        this.repository = repository;
        this.authorService = authorService;
    }
}