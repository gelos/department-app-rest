package com.example.demo.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Department {

  private @Id @GeneratedValue long id;
  
  private String name;
  
  @OneToMany(mappedBy="department_Id")
  private List<Employee> employees;

}
