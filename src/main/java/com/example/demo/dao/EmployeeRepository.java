package com.example.demo.dao;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.example.demo.model.Department;
import com.example.demo.model.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

  List<Employee> findByBornDate(Date bornDate);

  List<Employee> findByBornDateBetween(Date bornDateStart, Date bornDateEnd);
  
  List<Employee> findBySecondName(String secondName);
  
  //@Query("SELECT AVG(salary) FROM Employee e WHERE e.DEPARTMENT_ID = ?1")
  //@Query("SELECT AVG(salary) FROM Employee")
  //Double getAvgSalary(Long department_id);
  /*@Query("SELECT AVG(salary) FROM Employee e WHERE e.department = ?1")
  
  "SELECT AVG(e.salary) FROM Department d LEFT JOIN d.employee e " +
  "WHERE a.address = :address";
  
  Double getAvgSalary(Department department);*/

}
