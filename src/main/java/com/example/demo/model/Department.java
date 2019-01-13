package com.example.demo.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class Department extends BaseEntity{
   
  public Department(String name) {
    this.name = name;
  }
  
  private String name;
  
  @OneToMany(mappedBy="department")
  private List<Employee> employees;

}
