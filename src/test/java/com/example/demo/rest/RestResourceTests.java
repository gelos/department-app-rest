package com.example.demo.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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
  public void givenDepartment_thenReturnJsonArray() throws Exception {

    // given
    Department department = departmentRepository.save(new Department("test department"));

    // then
    mockMvc
        .perform(get(basePath + "/departments/" + department.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.name", is(department.getName())));

  }

  @Test
  public void givenDepartment_thenReturnJsonArrayDepartmentWithCyrrilic() throws Exception {

    // given
    Department department = departmentRepository.save(new Department("тестовый департамент"));

    // then
    mockMvc
        .perform(get(basePath + "/departments/" + department.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.name", is(department.getName())));

  }

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

    MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.put(basePath + "/departments/{id}", department.getId())
            .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8").content(objectMapper.writeValueAsString(department));
            
    mockMvc.perform(builder)
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.name", is(secondName)));
  }
      
  @Test
  public void putDepartmentWithEmployee() throws Exception {

    // given
    String depName = "тестовый департамент";
    String newDepName = depName + "abc";
    Department department = departmentRepository.save(new Department(depName));
    Employee employee = employeeRepository.save(new Employee("Petya", "Sergeevich", "Пупкин", LocalDate.of(1999, 03, 16), 1000, department));

    // then
    ObjectMapper objectMapper = new ObjectMapper();
    department.setName(newDepName);

    assertThat(department.getName()).isNotEqualTo(departmentRepository.findById(department.getId()).get().getName());
    
    /*System.err.println(department.getName());
    System.err.println(departmentRepository.findById(department.getId()).get().getName());
    */
    
    System.err.println(department);
    System.err.println(department.getId());
    System.err.println(objectMapper.writeValueAsString(department));

    MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.put(basePath + "/departments/{id}", department.getId())
            .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8").content(objectMapper.writeValueAsString(department));
            
    mockMvc.perform(builder)
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.name", is(newDepName)));
  }  

}
