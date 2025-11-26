package com.luv2code.springboot.cruddemo.dao;

import com.luv2code.springboot.cruddemo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

//JpaRepository<Employee, Integer> - Entity type is the Employee and primary key is the Integer
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}
