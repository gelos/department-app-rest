package com.example.demo.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@ToString

public class Employee extends BaseEntity{
 
  private String firstName;
  
  private String patronymic;
  
  private String secondName;
  
  @Temporal(TemporalType.DATE)
  private Date bornDate;
  
  private int salary;
  
  @ManyToOne()
  @JoinColumn(name="department_Id")
  private Department department;
  
}
