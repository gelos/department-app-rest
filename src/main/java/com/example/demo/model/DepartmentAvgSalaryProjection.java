package com.example.demo.model;

import java.util.List;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "avgSalary", types = {Department.class})
public interface DepartmentAvgSalaryProjection {

  default Double getAvgSalary() {
    return getEmployees().stream().mapToDouble(Employee::getSalary).average().orElse(Double.NaN);
  }
  
  String getName();
  
  List<Employee> getEmployees();
    
}
