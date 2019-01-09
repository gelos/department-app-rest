package com.example.demo.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

  protected Employee() {
    
  }
  
  public Employee(Long department_Id) {
    this.department_Id = department_Id;
    this.firstName = "Vasya";
    this.patronymic = "Vasilevich";
    this.secondName = "Pupkin";
    
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    try {
      this.bornDate = formatter.parse("18/01/1978");
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
        
    this.salary = 1000;
    
  }
    
  private @Id @GeneratedValue long id; 
  
  private long department_Id;
  
  private String firstName;
  
  private String patronymic;
  
  private String secondName;
  
  @Temporal(TemporalType.DATE)
  private Date bornDate;
  
  private int salary;
  
}
