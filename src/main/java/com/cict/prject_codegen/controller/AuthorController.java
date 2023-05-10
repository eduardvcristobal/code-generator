package com.cict.prject_codegen.controller;

import com.cict.core.base.FilterRequest;
import com.cict.prject_codegen.dto.AuthorDTO;
import com.cict.prject_codegen.dto.BookDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Api(tags = "Author API")
public interface AuthorController {

    @ApiOperation("Add new data")
    public ResponseEntity<?> createOne(@RequestBody AuthorDTO dto);

    @ApiOperation("Add new data")
    public ResponseEntity<?> save(@RequestBody AuthorDTO book);

    @ApiOperation("Find by Id")
    public ResponseEntity<?> findById(@PathVariable("id") Long id);

    @ApiOperation("Delete based on primary key")
    public ResponseEntity<?> delete(@PathVariable("id") Long id);

    @ApiOperation("Find all data")
    public ResponseEntity<?> list();

    @ApiOperation("Pagination request")
    public ResponseEntity<?> pageQuery(Pageable pageable);

    @ApiOperation("Update one data")
    public ResponseEntity<?> update(@RequestBody AuthorDTO dto, @PathVariable("id") Long id);

    @ApiOperation("Filter page")
    public ResponseEntity<?> filterPage(Pageable pageable, FilterRequest filter);

    @ApiOperation("Find All Specs As List")
    public ResponseEntity<?> filterList(FilterRequest filter);

    @ApiOperation("Find All Specs As List With Fields")
    public ResponseEntity<?> findAllSpecsAsPageWithFields(FilterRequest filterRequest);

    @ApiOperation("Save author with books and address")
    public ResponseEntity<?> saveAuthorWithBooks(AuthorDTO authorDto);


}