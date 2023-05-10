package com.cict.prject_codegen.dao;

import com.cict.prject_codegen.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository()
public interface EmployeeRepository extends BaseRepository<Employee, Long> {
}