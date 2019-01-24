package com.example.demo.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.example.demo.dao.DepartmentRepository;
import com.example.demo.model.Department;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc // <-- this is the fix
public class RestResourceTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Test
  public void givenEmployees_whenGetEmployees_thenReturnJsonArray() throws Exception {

    Department department = departmentRepository.save(new Department("test department"));

    mockMvc
        .perform(get("/departments/" + department.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.name", is(department.getName())));

  }

}
