
package com.incubyte.salarymanagement;

import jakarta.persistence.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

@Entity
@Table(name = "employees")
public class Employee {

    
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@NotBlank(message = "Employee name is required")
@Column(nullable = false)
private String name;

@NotBlank(message = "Email is required")
@Email(message = "Please enter a valid email address")
@Column(nullable = false, unique = true)
private String email;

@NotBlank(message = "Department is required")
@Column(nullable = false)
private String department;

@NotNull(message = "Salary is required")
@DecimalMin(value = "0.0", inclusive = false,
            message = "Salary must be greater than zero")
@Column(nullable = false)
private BigDecimal salary;


    public Employee() {
    }

    
public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getEmail() {
    return email;
}

public void setEmail(String email) {
    this.email = email;
}

public String getDepartment() {
    return department;
}

public void setDepartment(String department) {
    this.department = department;
}

public BigDecimal getSalary() {
    return salary;
}

public void setSalary(BigDecimal salary) {
    this.salary = salary;
}

}
