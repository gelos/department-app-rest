package com.example.demo.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@ToString
@Getter
@Setter
public class Department extends BaseEntity {

  @NonNull
  private String name;

  @OneToMany(mappedBy = "department")
  private List<Employee> employees;

  // First clear employees association.
  // Need to allow DELETE department with associated employees
  @PreRemove
  private void preRemove() {
    for (Employee employee : this.employees) {
      employee.setDepartment(null);
    }
  }

  @PrePersist
  @PreUpdate
  public void updateEmployeeAssociation() {
    if (this.employees != null) {
      for (Employee employee : this.employees) {
        if (employee.getDepartment() == null) {
          System.err.println(this);
          employee.setDepartment(this);
        }
      }
    }
  }


  // Method 1. Average salary calculation. Using public method. Jackson automatically serialize
  // public fields.
  // @JsonIgnore
  // public Double getAvgSalary() {
  // @Getter
  /*
   * private Double avgSalary() { return
   * employees.stream().mapToDouble(Employee::getSalary).average().orElse(Double.NaN); }
   */

}
