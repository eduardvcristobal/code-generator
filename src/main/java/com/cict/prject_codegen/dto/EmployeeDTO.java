package com.cict.prject_codegen.dto;

import com.cict.prject_codegen.base.Mergeable;
import com.cict.prject_codegen.model.Employee;

public class EmployeeDTO extends BaseDTO<Employee>  {  //extends AbstractDTO<Long> {
    // abstract or virtual EmployeeDTO will not implement the abstract method getId()
    // in AbstractDTO<Long> if the getId type is the same as the generic type
    private Long id;
    private String name;
    private String dept;
    private double salary;

    public EmployeeDTO() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getDept() {
        return this.dept;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getSalary() {
        return this.salary;
    }
}