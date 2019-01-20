package com.example.demo.dao;

import org.springframework.data.repository.CrudRepository;
import com.example.demo.model.Department;

public interface DepartmentRepository extends CrudRepository<Department, Long>{
  
}
