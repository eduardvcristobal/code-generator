package com.cict.prject_codegen.controller.impl;

import com.cict.core.base.FilterRequest;
import com.cict.prject_codegen.controller.AuthorController;
import com.cict.prject_codegen.dto.AuthorDTO;
import com.cict.prject_codegen.dto.BookDTO;
import com.cict.prject_codegen.model.Author;
import com.cict.prject_codegen.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/author")
@RestController
public class AuthorControllerImpl implements AuthorController {

    private final AuthorService service;

    public AuthorControllerImpl(AuthorService service
    ) {
        this.service = service;
    }

    @Override
    @PostMapping("/createOne")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createOne(AuthorDTO dto) {
        return ResponseEntity.ok(service.createOne(1L, dto));
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody AuthorDTO dto) {
        Author author = service.asEntity(dto, Author.class);
        /*//getAuthor
        Author author = service.findById(dto.getAuthor().getId())
                .orElseThrow( () -> new RuntimeException("Author not found"));
        //set author
        author.setAuthor(author);*/
        return ResponseEntity.ok( service.save(author) );
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@RequestBody List<AuthorDTO> bookDTOList) {
        return ResponseEntity.ok(service.saveAll(bookDTOList, Author.class, AuthorDTO.class));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Author author = service.findById(id).orElse(null);
        return ResponseEntity.ok(author); //modelMapper.map(author, AuthorDTO.class);
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
        final List<Author> all = service.findAll();
        return ResponseEntity.ok(all);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody AuthorDTO dto, @PathVariable("id") Long id) {
        final Object map = service.update(dto, id, AuthorDTO.class);
        return ResponseEntity.ok(map);
    }

    //http://example.com/page-query?page=0&size=10&sort=id,desc
    @Override
    @GetMapping("/page-query")
    public ResponseEntity<?> pageQuery(Pageable pageable) {
        Page<Author> all = service.findAll(pageable);
        return ResponseEntity.ok(all);
    }

    @Override
    @PostMapping("/filter-page")
    public ResponseEntity<?> filterPage(Pageable pageable, final @RequestBody @Valid FilterRequest filterRequest) {
        Page<Author> bookPage = service.findAllSpecsAsPage(filterRequest, pageable);
        return ResponseEntity.ok(bookPage);
    }

    @Override
    @PostMapping("/filter-list")
    public ResponseEntity<?> filterList(final @RequestBody @Valid FilterRequest filter) {
        final List<Author> allSpecsAsList = service.findAllSpecsAsList(filter);
        return ResponseEntity.ok(allSpecsAsList);
    }

    @Override
    @PostMapping("/filter-list-fields")
    public ResponseEntity<?> findAllSpecsAsPageWithFields(final @RequestBody @Valid FilterRequest filterRequest) {
        List<?> books = service.findAllSpecsAsListWithFields(filterRequest, Author.class);
        return ResponseEntity.ok(books);
    }

    @Override
    @PostMapping("/saveWithBooks")
    public ResponseEntity<?> saveAuthorWithBooks(@RequestBody @Valid AuthorDTO authorDto) {
        return ResponseEntity.ok( service.saveAuthorWithBooks(authorDto));
    }

}