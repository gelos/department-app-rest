package com.example.demo.rest;

import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.example.demo.dao.DepartmentRepository;
import com.example.demo.dao.EmployeeRepository;
import com.example.demo.model.Department;
import com.example.demo.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc // <-- this is the fix
public class RestResourceTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Value("${spring.data.rest.basePath}")
  private String basePath;


  @Test
  public void getDepartmentWithCyrrilicName() throws Exception {

    // given
    Department department = departmentRepository.save(new Department("тестовый департамент"));

    // then
    mockMvc
        .perform(get(basePath + "/departments/{id}", department.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.name", is(department.getName())));

  }

  @Test
  public void getDepartmentAndEmployee() throws Exception {

    // given
    Department department = new Department("test отдел");
    Employee employee =
        new Employee("Petya", "Sergeevich", "Пупкин", LocalDate.of(1999, 03, 16), 1000, null);

    department.setEmployees((List<Employee>) Arrays.asList(employee));
    employee.setDepartment(department);

    departmentRepository.save(department);
    employeeRepository.save(employee);

    // when
    String depPath = basePath + "/departments/" + department.getId();
    String depPathWithEmp = depPath + "/employees";
    String empPath = basePath + "/employees/" + employee.getId();

    // then

    // get department
    mockMvc.perform(get(depPath).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(department.getName())))
        .andExpect(jsonPath("_links.employees.href", containsString(depPathWithEmp)))
        .andDo(print());

    // get the department employees list
    mockMvc.perform(get(depPathWithEmp).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("_embedded.employees", hasSize(1)))
        .andExpect(jsonPath("_embedded.employees[0].firstName", is(employee.getFirstName())))
        .andExpect(jsonPath("_embedded.employees[0].salary", is(employee.getSalary()))).andExpect(
            jsonPath("_embedded.employees[0]._links.department.href", containsString(empPath)));

    // get employee
    mockMvc.perform(get(empPath).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(jsonPath("$.secondName", is(employee.getSecondName())))
        .andExpect(jsonPath("$.patronymic", is(employee.getPatronymic())));

  }

  @Ignore
  @Test
  public void putDepartment() throws Exception {

    // given
    String firstName = "тестовый департамент";
    String secondName = firstName + "abc";
    Department department = departmentRepository.save(new Department(firstName));

    // then
    ObjectMapper objectMapper = new ObjectMapper();
    department.setName(secondName);

    System.err.println(department);
    System.err.println(department.getId());
    System.err.println(objectMapper.writeValueAsString(department));


    mockMvc
        .perform(get(basePath + "/departments/" + department.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.name", is(department.getName())));

    MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.put(basePath + "/departments/{id}", department.getId())
            .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8").content(objectMapper.writeValueAsString(department));

    mockMvc.perform(builder).andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(secondName)));
  }

  @Ignore
  @Test
  public void putDepartmentWithEmployee() throws Exception {

    // given
    Department department = new Department("тестовый отдел");
    Employee employee =
        new Employee("Petya", "Sergeevich", "Пупкин", LocalDate.of(1999, 03, 16), 1000, null);

    department.setEmployees((List<Employee>) Arrays.asList(employee));
    employee.setDepartment(department);

    departmentRepository.save(department);
    employeeRepository.save(employee);

    // when
    String depPath = basePath + "/departments/" + department.getId();
    String depPathWithEmp = depPath + "/employees";
    String empPath = basePath + "/employees/" + employee.getId();
    
    ObjectMapper objectMapper = new ObjectMapper();
    
    //then
    
    // put department
    MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.put(depPath)
            .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8").content(objectMapper.writeValueAsString(department));

    mockMvc.perform(builder).andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(department.getName())));    

    // put employee
    builder =
        MockMvcRequestBuilders.put(depPath)
            .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8").content(objectMapper.writeValueAsString(department));

    mockMvc.perform(builder).andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(department.getName())));    

    
    
  }

}
