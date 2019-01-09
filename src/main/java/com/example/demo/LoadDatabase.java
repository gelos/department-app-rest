package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.demo.dao.DepartmentRepository;
import com.example.demo.dao.EmployeeRepository;
import com.example.demo.model.Department;
import com.example.demo.model.Employee;

@Configuration
public class LoadDatabase {
  
  @Bean
  CommandLineRunner initDatabase (EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
    return args -> {
      Department dep1 = departmentRepository.save(new Department("dep1"));
      Department dep2 = departmentRepository.save(new Department("dep2"));
      Department dep3 = departmentRepository.save(new Department("dep3"));
      employeeRepository.save(new Employee(dep1.getId()));
      employeeRepository.save(new Employee(dep2.getId()));
      employeeRepository.save(new Employee(dep3.getId()));
    };
    
  }

}
