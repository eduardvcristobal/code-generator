package com.cict.prject_codegen.controller.impl;

import com.cict.core.base.FilterRequest;
import com.cict.prject_codegen.controller.BookController;
import com.cict.prject_codegen.dto.BookDTO;
import com.cict.prject_codegen.model.Author;
import com.cict.prject_codegen.model.Book;
import com.cict.prject_codegen.service.AuthorService;
import com.cict.prject_codegen.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/book")
@RestController
public class BookControllerImpl implements BookController {
    private final BookService service;
    private final AuthorService authorService;

    public BookControllerImpl(
            BookService service,
            AuthorService authorService) {
        this.service = service;
        this.authorService = authorService;
    }

    @Override
    @PostMapping("/createOne")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createOne(BookDTO dto) {
        return ResponseEntity.ok(service.createOne(1L, dto));
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody BookDTO bookDTO) {
        Book book = service.asEntity(bookDTO, Book.class);
        //getAuthor
        Author author = authorService.findById(bookDTO.getAuthor().getId())
                .orElseThrow( () -> new RuntimeException("Author not found"));
        //set author
        book.setAuthor(author);
        return ResponseEntity.ok( service.save(book) );
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@RequestBody List<BookDTO> bookDTOList) {
        return ResponseEntity.ok(service.saveAll(bookDTOList, Book.class, BookDTO.class));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Book book = service.findById(id).orElse(null);
        return ResponseEntity.ok(book); //modelMapper.map(book, BookDTO.class);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping
    public ResponseEntity<?> list() {
        final List<Book> all = service.findAll();
        return ResponseEntity.ok(all);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody BookDTO bookDTO, @PathVariable("id") Long id) {
        final Object map = service.update(bookDTO, id, BookDTO.class);
        return ResponseEntity.ok(map);
    }

    //http://example.com/page-query?page=0&size=10&sort=id,desc
    @Override
    @GetMapping("/page-query")
    public ResponseEntity<?> pageQuery(Pageable pageable) {
        Page<Book> all = service.findAll(pageable);
        return ResponseEntity.ok(all);
    }

    @Override
    @PostMapping("/filter-page")
    public ResponseEntity<?> filterPage(Pageable pageable, final @RequestBody @Valid FilterRequest filterRequest) {
        Page<Book> bookPage = service.findAllSpecsAsPage(filterRequest, pageable);
        return ResponseEntity.ok(bookPage);
    }

    @Override
    @PostMapping("/filter-list")
    public ResponseEntity<?> filterList(final @RequestBody @Valid FilterRequest filter) {
        final List<Book> allSpecsAsList = service.findAllSpecsAsList(filter);
        return ResponseEntity.ok(allSpecsAsList);
    }


    @PostMapping("/filter-list-fields")
    public ResponseEntity<?> findAllSpecsAsPageWithFields(final @RequestBody @Valid FilterRequest filterRequest) {
        List<?> books = service.findAllSpecsAsListWithFields(filterRequest, Book.class);
        return ResponseEntity.ok(books);
    }

}
