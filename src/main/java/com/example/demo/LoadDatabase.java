package com.example.demo;

import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.example.demo.dao.DepartmentRepository;
import com.example.demo.dao.EmployeeRepository;
import com.example.demo.model.Department;
import com.example.demo.model.Employee;

@Configuration
public class LoadDatabase {
  
  @Bean
  @Profile("h2")
  CommandLineRunner initDatabase (EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
    return args -> {
      
      Department dep1 = departmentRepository.save(new Department("administration"));
      Department dep2 = departmentRepository.save(new Department("bookkeeping"));
      Department dep3 = departmentRepository.save(new Department("it"));
      Department dep4 = departmentRepository.save(new Department("департамент Всея Руси"));
      
      employeeRepository.save(new Employee("Ivan", "Ivanovich", "Ivanov", LocalDate.of(1978, 1, 18), 1000, dep1));
      employeeRepository.save(new Employee("Petya", "Petrovich", "Petrov", LocalDate.of(1958, 11, 8), 1100, dep2));
      employeeRepository.save(new Employee("Sidor", "Sidorovich", "Sidorov", LocalDate.of(1999, 3, 16), 1200, dep3));
      employeeRepository.save(new Employee("Andrey", "Andreevich", "Olyunin", LocalDate.of(2004, 5, 1), 1300, dep2));
      employeeRepository.save(new Employee("Sergey", "Sergeevich", "Pupkin", LocalDate.of(2008, 11, 21), 2300, dep2));
      employeeRepository.save(new Employee("Харлампий", "Егорович", "Зайцев", LocalDate.of(2018, 10, 20), 2300, dep4));
      
    };
    
  }

}
