package com.example.demo.dao;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import com.example.demo.model.Department;
import com.example.demo.model.Employee;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DepartmentRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  protected DepartmentRepository departmentRepository;

  @Autowired
  protected EmployeeRepository employeeRepository;


  @Test
  public void whenFindById_thenReturnDepartmentNameWithCyrrilic() {

    // given
    Department department = new Department("тестовый департамент");
    department = entityManager.persistAndFlush(department);

    // when
    Optional<Department> found = departmentRepository.findById(department.getId());

    // then
    assertThat(found.isPresent()).isTrue();
    assertThat(found.get().getName()).isEqualTo(department.getName());
  }

  
  @Test
  public void whenFindById_thenReturnDepartment() {

    // given
    Department department = new Department("test department");
    department = entityManager.persistAndFlush(department);

    // when
    Optional<Department> found = departmentRepository.findById(department.getId());

    // then
    assertThat(found.isPresent()).isTrue();
    assertThat(found.get().getName()).isEqualTo(department.getName());
  }

  //TODO Move test to REST layer tests.
  
/*  @Test
  public void checkAvgSalary() {

    // given
    Department department = entityManager.persistAndFlush(new Department("test department"));
    Department department2 = entityManager.persistAndFlush(new Department("test department2"));

    entityManager.persistAndFlush(
        new Employee("Ivan", "Ivanovich", "Ivanov", LocalDate.of(1978, 1, 18), 1000, department));
    entityManager.persistAndFlush(
        new Employee("Petya", "Petrovich", "Petrov", LocalDate.of(1958, 11, 8), 1100, department2));
    entityManager.persistAndFlush(new Employee("Sidor", "Sidorovich", "Sidorov",
        LocalDate.of(1999, 03, 16), 1200, department));
    entityManager.persistAndFlush(new Employee("Andrey", "Andreevich", "Olyunin",
        LocalDate.of(2004, 05, 1), 1300, department2));
    entityManager.persistAndFlush(new Employee("Sergey", "Sergeevich", "Pupkin",
        LocalDate.of(2008, 11, 21), 2300, department2));

    // when
    entityManager.refresh(department2); // fill employees list in department2 by refreshing state
                                        // from DB
    Optional<Department> found = departmentRepository.findById(department2.getId());

    Double avgSalary = StreamSupport.stream(employeeRepository.findAll().spliterator(), false)
        .filter(x -> department2.equals(x.getDepartment())).mapToDouble(Employee::getSalary)
        .average().orElse(Double.NaN);

    // then
    assertThat(found.isPresent()).isTrue();
    assertThat(found.get().getEmployees()).isNotNull();
    assertThat(found.get().getAvgSalary()).isEqualTo(avgSalary);
  }*/
}
