package com.example.demo.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import com.example.demo.model.Department;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DepartmentRepositoryTest {
 
    @Autowired
    private TestEntityManager entityManager;
 
    @Autowired
    private DepartmentRepository departmentRepository;
 
    @Test
    public void whenFindById_thenReturnDepartment() {
        // given
        Department department = new Department("test department");
        department = entityManager.persistAndFlush(department);
     
        // when
        Optional<Department> found = departmentRepository.findById(department.getId());
     
        // then
        assertTrue(found.isPresent());
        assertThat(found.get().getName())
          .isEqualTo(department.getName());
    }
 }