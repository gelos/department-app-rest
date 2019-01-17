package com.example.demo.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.example.demo.model.Department;
import com.example.demo.model.DepartmentProjection1;

@RepositoryRestResource(excerptProjection = DepartmentProjection1.class)
public interface DepartmentRepository extends CrudRepository<Department, Long>{
   
//@Query("SELECT AVG(salary) FROM Employee e WHERE e.department = ?1")
  
//@Query("SELECT AVG(e.salary) FROM Department d LEFT JOIN d.employees e WHERE d.id = ?1")
  
  //Double getAvgSalary(Long department_id);
  
}
