package com.example.demo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@ToString

public class Employee extends BaseEntity {

  private String firstName;

  private String patronymic;

  private String secondName;

  private LocalDate bornDate;

  private int salary;

  @ManyToOne()
  @JoinColumn(name = "department_Id")
  @ToString.Exclude // to prevent StackOverflow error from bidirectional association between
                    // the department and the employee when printing an entity
  private Department department;

  // First clear department association.
  // Need to allow DELETE employee with associated department
  @PreRemove
  private void preRemove() {

    if (this.department != null) {
      List<Employee> employees = new ArrayList<Employee>(this.department.getEmployees());
      employees.remove(this);
      this.department.setEmployees(employees);
      this.department = null;
    }

  }

}
