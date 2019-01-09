package com.example.demo.dao;

import java.util.Date;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.example.demo.model.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

  List<Employee> findByBornDate(Date bornDate);

  List<Employee> findByBornDateBetween(Date bornDateStart, Date bornDateEnd);
  
  List<Employee> findBySecondName(String secondName);

}
