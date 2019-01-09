package com.example.demo.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
public class Employee {

  private @Id @GeneratedValue long id; 
  
  private long department_Id;
  
  private String firstName;
  
  private String patronymic;
  
  private String secondName;
  
  @Temporal(TemporalType.DATE)
  private Date bornDate;
  
  private int salary;
  
}
