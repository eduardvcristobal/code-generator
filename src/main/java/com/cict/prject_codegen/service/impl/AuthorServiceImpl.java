package com.cict.prject_codegen.service.impl;

import com.cict.prject_codegen.dao.AddressRepository;
import com.cict.prject_codegen.dao.AuthorRepository;
import com.cict.prject_codegen.dao.BookRepository;
import com.cict.prject_codegen.dto.AddressDTO;
import com.cict.prject_codegen.dto.AuthorDTO;
import com.cict.prject_codegen.dto.AuthorWithBooksDTO;
import com.cict.prject_codegen.dto.BookDTO;
import com.cict.prject_codegen.model.Address;
import com.cict.prject_codegen.model.Author;
import com.cict.prject_codegen.model.Book;
import com.cict.prject_codegen.service.AuthorService;
import com.cict.prject_codegen.base.MySpecification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorServiceImpl extends MySpecification<Author, AuthorDTO, AuthorRepository> implements AuthorService {

    private final AuthorRepository repository;
    private final BookRepository bookRepository;
    private final AddressRepository addressRepository;

    public AuthorServiceImpl(AuthorRepository repository, BookRepository bookRepository,
                             AddressRepository addressRepository) {
        super(repository, Author.class, AuthorDTO.class);
        this.repository = repository;
        this.bookRepository = bookRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public Object saveAuthorWithBooks(AuthorDTO authorDto) {

        Address address= addressRepository.save(authorDto.getAddress());

        // Create and save the Author entity
        Author author = modelMapper.map(authorDto, Author.class);

        // Create and save the Book entities
        List<Book> bookDtos = authorDto.getBooks();
        List<Book> books = new ArrayList<>();
        for (Book bookDto : bookDtos) {
            Book book = modelMapper.map(bookDto, Book.class);
            book.setAuthor(author);
            books.add(book);
        }
//        List<Book> savedBooks = bookRepository.saveAll(books);
        author.setBooks(books); //set the books to the author
        Author savedAuthor = repository.save(author);
        savedAuthor.setAddress(address);
        repository.save(savedAuthor);

        // Update the Author entity with the saved books
        //remove this if cascade is used
        /*savedAuthor.setBooks(savedBooks);
        repository.save(author);*/

        // Convert and return the saved Author with its books
        AuthorWithBooksDTO authorWithBooksDto = new AuthorWithBooksDTO().from(author);
        authorWithBooksDto.setAddress( new AddressDTO(address.getId(), author.getAddress().getStreet()));
        authorWithBooksDto.setBooks(books.stream().map(book -> new BookDTO(book.getId(), book.getTitle())).collect(Collectors.toList()));
        return authorWithBooksDto;
    }
}