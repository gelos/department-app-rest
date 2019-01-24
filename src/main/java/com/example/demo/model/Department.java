package com.example.demo.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;
import lombok.AccessLevel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Department extends BaseEntity {

  @NonNull
  private String name;

  @OneToMany(mappedBy = "department")
  //private List<Employee> employees;
  private List<Employee> employees;

  // Method 1. Average salary calculation. Using public method. Jackson automatically serialize
  // public fields.
  public Double getAvgSalary() {
    return employees.stream().mapToDouble(Employee::getSalary).average().orElse(Double.NaN);
  }

}
