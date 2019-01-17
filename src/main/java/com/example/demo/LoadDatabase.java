package com.example.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
      
      Date bornDate1 = new Date();
      Date bornDate2 = new Date();
      Date bornDate3 = new Date();
      Date bornDate4 = new Date();
      Date bornDate5 = new Date();
      
      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
      try {
        bornDate1 = formatter.parse("18/01/1978");
        bornDate2 = formatter.parse("08/11/1958");
        bornDate3 = formatter.parse("16/03/1999");
        bornDate4 = formatter.parse("01/05/2004");
        bornDate5 = formatter.parse("21/11/2008");
        
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
            
      
      Employee emp1 = employeeRepository.save(new Employee("Ivan", "Ivanovich", "Ivanov", bornDate1, 1000, dep1));
      employeeRepository.save(new Employee("Petya", "Petrovich", "Petrov", bornDate2, 1100, dep2));
      employeeRepository.save(new Employee("Sidor", "Sidorovich", "Sidorov", bornDate3, 1200, dep3));
      employeeRepository.save(new Employee("Andrey", "Andreevich", "Olyunin", bornDate4, 1300, dep2));
      employeeRepository.save(new Employee("Sergey", "Sergeevich", "Pupkin", bornDate5, 2300, dep2));
      
      
            
    };
    
  }

}
