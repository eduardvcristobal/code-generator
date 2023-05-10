package com.cict.prject_codegen.controller;

import com.cict.core.base.Filter;
import com.cict.core.base.FilterRequest;
import com.cict.prject_codegen.dto.BookDTO;
import com.cict.prject_codegen.model.Book;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Book API")
public interface BookController {

    @ApiOperation("Add new data")
    public ResponseEntity<?> createOne(@RequestBody BookDTO dto);

    @ApiOperation("Add new data")
    public ResponseEntity<?> save(@RequestBody BookDTO dto);

    @ApiOperation("Find by Id")
    public ResponseEntity<?> findById(@PathVariable("id") Long id);

    @ApiOperation("Delete based on primary key")
    public ResponseEntity<?> delete(@PathVariable("id") Long id);

    @ApiOperation("Find all data")
    public ResponseEntity<?> list();

    @ApiOperation("Pagination request")
    public ResponseEntity<?> pageQuery(Pageable pageable);

    @ApiOperation("Update one data")
    public ResponseEntity<?> update(@RequestBody BookDTO dto, @PathVariable("id") Long id);

    @ApiOperation("Filter page")
    public ResponseEntity<?> filterPage(Pageable pageable, FilterRequest filter);

    @ApiOperation("Find All Specs As List")
    public ResponseEntity<?> filterList(FilterRequest filter);

    @ApiOperation("Find All Specs As List With Fields")
    public ResponseEntity<?> findAllSpecsAsPageWithFields(FilterRequest filterRequest);
}