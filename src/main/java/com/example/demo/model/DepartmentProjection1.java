package com.example.demo.model;

import java.util.List;
import org.springframework.data.rest.core.config.Projection;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Projection(name = "avgSalaryDepartment", types = {Department.class})

public interface DepartmentProjection1 {

  String getName();

  //@Value("#{target.getAvgSalary1()}")
  //Double getAvgSalary1();

  @JsonIgnore
  List<Employee> getEmployees();

  // Method 2. Average salary calculation. Using public method in projection.
  public default Double getAvgSalary2() {
    return getEmployees().stream().mapToDouble(Employee::getSalary).average().orElse(Double.NaN);
    /*Double result = new Double(0);
    for (Employee e : getEmployees()) {
      result += e.getSalary();
    }
    return result;*/
  }
}
