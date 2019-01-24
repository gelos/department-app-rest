package com.example.demo.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

  @Test
  public void checkAvgSalary() {

    // given
    Department department = entityManager.persistAndFlush(new Department("test department"));
    Department department2 = entityManager.persistAndFlush(new Department("test department2"));

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

    entityManager
        .persistAndFlush(new Employee("Ivan", "Ivanovich", "Ivanov", bornDate1, 1000, department));
    entityManager.persistAndFlush(
        new Employee("Petya", "Petrovich", "Petrov", bornDate2, 1100, department2));
    entityManager.persistAndFlush(
        new Employee("Sidor", "Sidorovich", "Sidorov", bornDate3, 1200, department));
    entityManager.persistAndFlush(
        new Employee("Andrey", "Andreevich", "Olyunin", bornDate4, 1300, department2));
    entityManager.persistAndFlush(
        new Employee("Sergey", "Sergeevich", "Pupkin", bornDate5, 2300, department2));

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
  }
}
