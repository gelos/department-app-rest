package com.example.demo.rest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.UnsupportedEncodingException;
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
import org.springframework.hateoas.MediaTypes;
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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc // <-- this is the fix
@Transactional
public class RestResourceTests {

  private static final String EMPLOYEES_BASE = "employees";

  private static final String DEPARTMENTS_BASE = "departments";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Value("${spring.data.rest.basePath}")
  private String baseLink;

  private Department dep1;
  private Department dep2;
  private Employee emp1;
  private Employee emp2;
  private Employee emp3;
  private ObjectMapper objectMapper;

  @Before
  public void setUp() {

    dep1 = new Department("test отдел");
    dep2 = new Department("отдел номер 2");

    emp1 = new Employee("Petya", "Sergeevich", "Пупкин", LocalDate.of(1999, 03, 16), 1000, null);
    emp2 = new Employee("Харлампий", "Егорович", "Зайцев", LocalDate.of(2018, 10, 20), 2300, null);
    emp3 = new Employee("Andrey", "Andreevich", "Olyunin", LocalDate.of(2004, 5, 1), 1300, null);

    dep2.setEmployees((List<Employee>) Arrays.asList(emp1, emp2));
    emp1.setDepartment(dep2);
    emp2.setDepartment(dep2);

    departmentRepository.save(dep1);
    departmentRepository.save(dep2);
    employeeRepository.save(emp1);
    employeeRepository.save(emp2);
    employeeRepository.save(emp3);

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // objectMapper.registerModule(new Jackson2HalModule());
  }

  @Test
  public void getEmployee() throws Exception {

    // given

    Employee expectedEmployee = emp2;
    Employee actualEmployee;

    // when

    MvcResult mvcResult =
        mockMvc.perform(get(baseLink + "/employees/{id}", expectedEmployee.getId())
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

    actualEmployee =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Employee.class);


    // then

    // Check only field which objectMapper can successful deserialize
    // Ignore id and department property which deserialize as null and cannot be equal to actual
    // Employee properties
    // For that reason do not use Object.equal()
    assertEquals(expectedEmployee.getFirstName(), actualEmployee.getFirstName());
    assertEquals(expectedEmployee.getPatronymic(), actualEmployee.getPatronymic());
    assertEquals(expectedEmployee.getSecondName(), actualEmployee.getSecondName());
    assertEquals(expectedEmployee.getBornDate(), actualEmployee.getBornDate());
    assertEquals(expectedEmployee.getSalary(), actualEmployee.getSalary());

  }

  @Test
  public void getDepartment() throws Exception {

    // given
    Department expectedDepartment = dep2;
    Department actualDepartment;

    // when
    MvcResult mvcResult =
        mockMvc.perform(get(baseLink + "/departments/{id}", expectedDepartment.getId())
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
    actualDepartment =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Department.class);

    // then
    assertEquals(expectedDepartment.getName(), actualDepartment.getName());

  }

  @Test
  public void deleteEmployee_ThenCheckLinkedDepartmentExist() throws Exception {

    // given
    Employee expectedEmployee = emp2;
    String deleteEmployeeLink = baseLink + "/" + EMPLOYEES_BASE + "/" + expectedEmployee.getId();

    // when

    // get employee link to department
    MvcResult mvcResult = mockMvc.perform(get(deleteEmployeeLink).contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isOk()).andReturn();
    // Use .expand().getHref to remove parameters from link
    String linkedDepartmentLink = new Link(getLink(mvcResult, "department")).expand().getHref();

    // get direct link to department
    mvcResult = mockMvc.perform(get(linkedDepartmentLink).contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isOk()).andReturn();
    String departmentLink = getLink(mvcResult, "self");

    // delete employee
    mockMvc.perform(delete(deleteEmployeeLink).contentType(MediaTypes.HAL_JSON))
        .andExpect(status().is2xxSuccessful());


    // then

    // check that employee does not exists
    mockMvc.perform(delete(deleteEmployeeLink).contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isNotFound());

    // check that linked department not deleted
    mvcResult = mockMvc.perform(get(departmentLink).contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isOk()).andReturn();

    // get link of the department employees list
    // Use .expand().getHref to remove parameters from link
    String linkedEmployeesLink = new Link(getLink(mvcResult, EMPLOYEES_BASE)).expand().getHref();

    // get list of the department employees
    mvcResult = mockMvc.perform(get(linkedEmployeesLink).contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isOk()).andReturn();

    // get lists of self link for all employees in department employee list
    List<String> employeesSelfHrefList =
        JsonPath.parse(mvcResult.getResponse().getContentAsString())
            .read("_embedded." + "employees[*]._links.self.href");

    // check that list has only one member (emp1)
    assertThat(employeesSelfHrefList, hasSize(1));

    // check that list not contain link to deleted employee (emp2)
    employeesSelfHrefList.forEach(
        expectedString -> assertThat(expectedString, not(containsString(deleteEmployeeLink))));

  }

  @Test
  public void deleteDepartment_ThenCheckLinkedEmployeeExist() throws Exception {

    // given
    Department expectedDepartment = dep2;
    String deleteDepartmentLink =
        baseLink + "/" + DEPARTMENTS_BASE + "/" + expectedDepartment.getId();


    // when

    // get link of the department employees list
    MvcResult mvcResult =
        mockMvc.perform(get(deleteDepartmentLink).contentType(MediaTypes.HAL_JSON))
            .andExpect(status().isOk()).andReturn();

    // Use .expand().getHref to remove parameters from link
    String linkedEmployeesLink = new Link(getLink(mvcResult, EMPLOYEES_BASE)).expand().getHref();

    // get list of the department employees
    mvcResult = mockMvc.perform(get(linkedEmployeesLink).contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isOk()).andReturn();

    // get lists of self link for all employees in department employee list
    List<String> employeesSelfHrefList =
        JsonPath.parse(mvcResult.getResponse().getContentAsString())
            .read("_embedded." + "employees[*]._links.self.href");

    // delete department
    mockMvc.perform(delete(deleteDepartmentLink).contentType(MediaTypes.HAL_JSON))
        .andExpect(status().is2xxSuccessful());


    // then

    // check that department is deleted
    mockMvc.perform(get(deleteDepartmentLink).contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isNotFound());

    // check that all employees of deleted department have no department
    for (String employeeLink : employeesSelfHrefList) {
      mvcResult = mockMvc.perform(get(employeeLink).contentType(MediaTypes.HAL_JSON))
          .andExpect(status().isOk()).andReturn();
      String linkedDepartmentLink = new Link(getLink(mvcResult, "department")).expand().getHref();

      mockMvc.perform(get(linkedDepartmentLink).contentType(MediaTypes.HAL_JSON))
          .andExpect(status().isNotFound());
    }
  }

  @Test
  public void postEmployee_ThenCheckLinkedDepartment() throws Exception {

    // given
    Employee newEmployee =
        new Employee("Sidor", "Sidorovich", "Сидоров", LocalDate.of(1999, 3, 16), 1200, null);
    Department linkedDepartment = dep1;

    String empBaseLink = baseLink + "/" + EMPLOYEES_BASE + "/";
    String departmentLink = baseLink + "/" + DEPARTMENTS_BASE + "/" + linkedDepartment.getId();

    // add department link to employee
    JsonNode newEmployeeJSON = objectMapper.valueToTree(newEmployee);
    ((ObjectNode) newEmployeeJSON).put("department", departmentLink);


    // when

    // post newEmployee with associated department
    MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(empBaseLink)
        .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaTypes.HAL_JSON)
        .characterEncoding("UTF-8").content(objectMapper.writeValueAsString(newEmployeeJSON));
    MvcResult mvcResult = mockMvc.perform(builder).andExpect(status().isCreated()).andReturn();

    // get link of the new employee
    String employeeLink = new Link(getLink(mvcResult, "self")).expand().getHref();

    // create association department with the new employee
    builder = MockMvcRequestBuilders.put(departmentLink + "/employees").contentType("text/uri-list")
        .accept(MediaTypes.HAL_JSON).characterEncoding("UTF-8").content(employeeLink);
    mockMvc.perform(builder).andExpect(status().is2xxSuccessful());


    // then

    // check created Employee
    Employee actualEmployee =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Employee.class);

    // Check only field which objectMapper can successful deserialize
    // Ignore id and department property which deserialize as null and cannot be equal to actual
    // Employee properties
    // For that reason do not use Object.equal()
    assertEquals(newEmployee.getFirstName(), actualEmployee.getFirstName());
    assertEquals(newEmployee.getPatronymic(), actualEmployee.getPatronymic());
    assertEquals(newEmployee.getSecondName(), actualEmployee.getSecondName());
    assertEquals(newEmployee.getBornDate(), actualEmployee.getBornDate());
    assertEquals(newEmployee.getSalary(), actualEmployee.getSalary());

    // check that linked department exists
    String linkedDepartmentLink = new Link(getLink(mvcResult, "department")).expand().getHref();
    mvcResult = mockMvc.perform(get(linkedDepartmentLink).contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isOk()).andReturn();

    // check that new employee associated to linked department
    String linkedEmployeesLink = new Link(getLink(mvcResult, "employees")).expand().getHref();
    mockMvc.perform(get(linkedEmployeesLink).contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("_embedded.employees", hasSize(1)))
        .andExpect(
            jsonPath("_embedded.employees[0]._links.self.href", containsString(employeeLink)));

  }

  @Test
  public void putDepartment() throws Exception {

    // put department
    // then check changed properties

    // then
    ObjectMapper objectMapper = new ObjectMapper();
    String oldName = dep1.getName();
    String newName = oldName + "123";
    dep1.setName(newName);

    MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.put(baseLink + "/departments/{id}", dep1.getId())
            .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8").content(objectMapper.writeValueAsString(dep1));

    mockMvc.perform(builder).andExpect(status().isOk()).andExpect(jsonPath("$.name", is(newName)))
        .andDo(print());

    dep1.setName(oldName);
  }

  @Test
  public void putEmployee() {
    assert (true);
  }

  // get value of the "rel" property in under the _links property
  private String getLink(MvcResult result, String rel) throws UnsupportedEncodingException {
    return JsonPath.parse(result.getResponse().getContentAsString())
        .read("_links." + rel + ".href");
  }

}
