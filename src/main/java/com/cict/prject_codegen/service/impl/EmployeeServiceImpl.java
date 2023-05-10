package com.cict.prject_codegen.service.impl;

import com.cict.prject_codegen.dao.EmployeeRepository;
import com.cict.prject_codegen.dto.EmployeeDTO;
import com.cict.prject_codegen.model.Employee;
import com.cict.prject_codegen.service.EmployeeService;
import com.cict.prject_codegen.base.MySpecification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class EmployeeServiceImpl extends MySpecification<Employee, EmployeeDTO, EmployeeRepository>  implements EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        super(repository, Employee.class, EmployeeDTO.class);
        this.repository = repository;
    }
}