package com.cict.prject_codegen.controller.impl;

import com.cict.core.base.FilterRequest;
import com.cict.prject_codegen.controller.EmployeeController;
import com.cict.prject_codegen.dto.EmployeeDTO;
import com.cict.prject_codegen.model.Employee;
import com.cict.prject_codegen.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/employee")
@RestController
public class EmployeeControllerImpl implements EmployeeController {
    private final EmployeeService employeeService;
    private final EmployeeService employeeMapper; //class depends only on the behavior specified in the interface
    private final ModelMapper modelMapper;

    public EmployeeControllerImpl(EmployeeService employeeService, EmployeeService employeeMapper,
                                  ModelMapper modelMapper) {
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody EmployeeDTO bookDTO) {
        Employee entity = employeeService.asEntity(bookDTO, Employee.class);
        return ResponseEntity.ok( employeeService.save(entity) );
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@RequestBody List<EmployeeDTO> bookDTOList) {
        return ResponseEntity.ok(employeeService.saveAll(bookDTOList, Employee.class, EmployeeDTO.class));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Employee entity = employeeService.findById(id).orElse(null);
        return ResponseEntity.ok(entity); //modelMapper.map(entity, EmployeeDTO.class);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {

        employeeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping
    public ResponseEntity<?> list() {
        final List<Employee> all = employeeService.findAll();
        return ResponseEntity.ok(all);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody EmployeeDTO bookDTO, @PathVariable("id") Long id) {
        final Object map = employeeService.update(bookDTO, id, EmployeeDTO.class);
        return ResponseEntity.ok(map);
    }

    //http://example.com/page-query?page=0&size=10&sort=id,desc
    @Override
    @GetMapping("/page-query")
    public ResponseEntity<?> pageQuery(Pageable pageable) {
        Page<Employee> all = employeeService.findAll(pageable);
        return ResponseEntity.ok(all);
    }

    @Override
    @PostMapping("/filter-page")
    public ResponseEntity<?> filterPage(Pageable pageable, final @RequestBody @Valid FilterRequest filterRequest) {
        Page<Employee> bookPage = employeeService.findAllSpecsAsPage(filterRequest, pageable);
        return ResponseEntity.ok(bookPage);
    }

    @Override
    @PostMapping("/filter-list")
    public ResponseEntity<?> filterList(final @RequestBody @Valid FilterRequest filter) {
        final List<Employee> allSpecsAsList = employeeService.findAllSpecsAsList(filter);
        return ResponseEntity.ok(allSpecsAsList);
    }


    @PostMapping("/filter-list-fields")
    public ResponseEntity<?> findAllSpecsAsPageWithFields(final @RequestBody @Valid FilterRequest filterRequest) {
        List<?> books = employeeService.findAllSpecsAsListWithFields(filterRequest, Employee.class);
        return ResponseEntity.ok(books);
    }

}
