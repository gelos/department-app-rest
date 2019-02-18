package com.example.demo.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonFormat;
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

  @PreRemove
  private void preRemove() {

    //this.department.getEmployees().remove(this);
    /*List<Employee> employees = this.department.getEmployees();
    this.department.setEmployees(null);

    int index = employees.indexOf(this);
    System.err.println(index);
    System.err.println(employees);
    if (index != -1) {
      employees.remove(index);
      System.err.println(employees);
      this.department.setEmployees(null);
    } ;
*/  
    }


}
