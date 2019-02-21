package com.example.demo.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dao.DepartmentRepository;
import com.example.demo.dao.EmployeeRepository;
import com.example.demo.model.Department;
import com.example.demo.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc // <-- this is the fix
@Transactional
public class RestResourceTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Value("${spring.data.rest.basePath}")
  private String basePath;

  private Department dep1;
  private Department dep2;
  private Employee emp1;
  private Employee emp2;
  private ObjectMapper objectMapper; 

  @Before
  public void setUp() {

    // given
    dep1 = new Department("test отдел");
    dep2 = new Department("отдел номер 2");

    emp1 = new Employee("Petya", "Sergeevich", "Пупкин", LocalDate.of(1999, 03, 16), 1000, null);
    emp2 = new Employee("Харлампий", "Егорович", "Зайцев", LocalDate.of(2018, 10, 20), 2300, null);

    dep2.setEmployees((List<Employee>) Arrays.asList(emp1, emp2));
    emp1.setDepartment(dep2);
    emp2.setDepartment(dep2);

    departmentRepository.save(dep1);
    departmentRepository.save(dep2);
    employeeRepository.save(emp1);
    employeeRepository.save(emp2);

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    
  }

  @Transactional
  @Test
  public void deleteDepartment() throws Exception {

    // when
    String deletePath = basePath + "/departments/" + dep2.getId();

    mockMvc.perform(get(deletePath).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.name", is(dep2.getName())))
        .andDo(print());

    assertThat(employeeRepository.findById(emp1.getId()).isPresent()).isTrue();
    assertThat(employeeRepository.findById(emp2.getId()).isPresent()).isTrue();
    assertThat(departmentRepository.findById(dep2.getId()).isPresent()).isTrue();

    // then
    mockMvc.perform(delete(deletePath).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful()).andDo(print());

    mockMvc.perform(get(deletePath).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound()).andDo(print());

    assertThat(employeeRepository.findById(emp1.getId()).isPresent()).isTrue();
    assertThat(employeeRepository.findById(emp2.getId()).isPresent()).isTrue();
    assertThat(departmentRepository.findById(dep2.getId()).isPresent()).isFalse();
    assertThat(employeeRepository.findById(emp1.getId()).get().getDepartment()).isNull();

  }

  @Transactional
  @Test
  public void deleteEmployee() throws Exception {

    // when
    String deletePath = basePath + "/employees/" + emp2.getId();

    mockMvc.perform(get(deletePath).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.firstName", is(emp2.getFirstName())))
        .andDo(print());

    assertThat(employeeRepository.findById(emp2.getId()).isPresent()).isTrue();
    assertThat(departmentRepository.findById(dep2.getId()).isPresent()).isTrue();

    // then
    mockMvc.perform(delete(deletePath).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful()).andDo(print());

    mockMvc.perform(delete(deletePath).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound()).andDo(print());

    assertThat(employeeRepository.findById(emp2.getId()).isPresent()).isFalse();
    assertThat(departmentRepository.findById(dep2.getId()).isPresent()).isTrue();
    assertThat(departmentRepository.findById(dep2.getId()).get().getEmployees().size())
        .isEqualTo(1);
  }

  @Test
  public void getDepartment() throws Exception {

    // then
    mockMvc
        .perform(get(basePath + "/departments/{id}", dep2.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.name", is(dep2.getName())));

  }

  @Test
  public void getEmployee() throws Exception {

    // then
    mockMvc
        .perform(get(basePath + "/employees/{id}", emp2.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.firstName", is(emp2.getFirstName())));

  }
 
  
  @Ignore
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
  
  @Test
  public void putDepartment() throws Exception {

    // then
    ObjectMapper objectMapper = new ObjectMapper();
    String oldName = dep1.getName();
    String newName = oldName + "123";
    dep1.setName(newName);
   
    MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.put(basePath + "/departments/{id}", dep1.getId())
            .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8").content(objectMapper.writeValueAsString(dep1));

    mockMvc.perform(builder).andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(newName))).andDo(print());
    
    dep1.setName(oldName);
  }

 
  @Test
  public void postNewEmployeeToDepartment() throws Exception {
    
    //given
    Employee emp3 = new Employee("Sidor", "Sidorovich", "Sidorov", LocalDate.of(1999, 3, 16), 1200, null);
    emp3.setDepartment(dep1);
    
    
   /* department.setEmployees((List<Employee>) Arrays.asList(employee));
    employee.setDepartment(department);

    departmentRepository.save(department);
    employeeRepository.save(employee);

    // when
    String depPath = basePath + "/departments/" + department.getId();
    String depPathWithEmp = depPath + "/employees";
  

*/
    // when
    String empBaseLink = basePath + "/employees/";
    String dep1Link = basePath + "/departments/" + dep1.getId();
    
    // then

    // post emp3
    MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(empBaseLink)
        .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8").content(objectMapper.writeValueAsString(emp3));

    MvcResult mvcResult = mockMvc.perform(builder).andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName", is(emp3.getFirstName()))).andReturn();

    String emp3Link = mvcResult.getResponse().getRedirectedUrl();

    // TODO get emp3 department
    mockMvc.perform(get(emp3Link).contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.firstName", is(emp3.getFirstName())))
    .andDo(print());
        
    
    // TODO get dep1 employee
    String mvcResponseStr = mvcResult.getResponse().getContentAsString();
    
    // remove the parameter part, Ex. http://localhost/api/rest/v1/employees/15/department{?projection}<-remove
/*    String emp3DepLink = new Link(JsonPath.parse(mvcResponseStr).read("_links.department.href")).expand().getHref();
    System.err.println(emp3DepLink);
    System.err.println(JsonPath.parse(mvcResponseStr).read("_links.department.href").toString());

     mockMvc.perform(get(emp3DepLink).contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isOk()).andExpect(jsonPath("$name", is(dep1.getName()))).andDo(print());
*/     
     
     
    /*.andExpect(jsonPath("_embedded.employees[0].firstName", is(emp2.getFirstName())))
    .andExpect(jsonPath("_embedded.employees[0].salary", is(emp2.getSalary()))).andExpect(
        jsonPath("_embedded.employees[0]._links.department.href", containsString(newEmployeePath)));*/

    
    /*
    System.err.println(newDepPath);
    System.err.println(new Link(newDepPath).expand().getHref());*/
    
    // get emp3 department
    
    /*String response = mvcResult.getResponse().getContentAsString();
    Integer  = JsonPath.parse(response).read("$[0].id");*/
    
    // get the department employees list
   /* mockMvc.perform(get(depPath).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("_embedded.employees", hasSize(1)))
        .andExpect(jsonPath("_embedded.employees[0].firstName", is(emp2.getFirstName())))
        .andExpect(jsonPath("_embedded.employees[0].salary", is(emp2.getSalary()))).andExpect(
            jsonPath("_embedded.employees[0]._links.department.href", containsString(newEmployeePath)));*/
    
    // put association

    /*builder = MockMvcRequestBuilders.post(empPath)
        .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)
        .characterEncoding("UTF-8").content(objectMapper.writeValueAsString(emp3));

    mockMvc.perform(builder).andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName", is(emp3.getFirstName()))).andDo(print());

*/    
    /*
    
    // put employee
    builder = MockMvcRequestBuilders.put(depPath).contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
        .content(objectMapper.writeValueAsString(department));

    mockMvc.perform(builder).andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(department.getName())));*/

  }

}
