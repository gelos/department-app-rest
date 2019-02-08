package com.example.demo.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import com.example.demo.dao.DepartmentRepository;
import com.example.demo.model.Department;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc // <-- this is the fix
public class RestResourceTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private DepartmentRepository departmentRepository;

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
    
    mockMvc
        .perform((RequestBuilder) ((ResultActions) put(basePath + "/departments/" + department.getId())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(department)))
        .andExpect(status().isOk()));
  }

}
