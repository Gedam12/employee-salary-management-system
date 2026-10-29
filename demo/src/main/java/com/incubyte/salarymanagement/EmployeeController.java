
package com.incubyte.salarymanagement;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    

@PostMapping
public Employee createEmployee(@Valid @RequestBody Employee employee) {
    return employeeRepository.save(employee);
}


@PutMapping("/{id}")
public Employee updateEmployee(
        @PathVariable Long id,
        @Valid @RequestBody Employee updatedEmployee) {

    Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Employee not found"
            ));

    employee.setName(updatedEmployee.getName());
    employee.setEmail(updatedEmployee.getEmail());
    employee.setDepartment(updatedEmployee.getDepartment());
    employee.setSalary(updatedEmployee.getSalary());

    return employeeRepository.save(employee);
}


@DeleteMapping("/{id}")
public void deleteEmployee(@PathVariable Long id) {

    if (!employeeRepository.existsById(id)) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Employee not found"
        );
    }

    employeeRepository.deleteById(id);
}


@GetMapping("/{id}")
public Employee getEmployeeById(@PathVariable Long id) {
    return employeeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Employee not found"
            ));
}


@PutMapping("/{id}/salary/increase")
public Employee increaseSalary(
        @PathVariable Long id,
        @RequestBody BigDecimal percentage) {

    if (percentage == null ||
            percentage.compareTo(BigDecimal.ZERO) <= 0 ||
            percentage.compareTo(new BigDecimal("100")) > 0) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Percentage must be greater than 0 and at most 100"
        );
    }

    Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Employee not found"
            ));

    BigDecimal increase = employee.getSalary()
            .multiply(percentage)
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

    employee.setSalary(employee.getSalary().add(increase));

    return employeeRepository.save(employee);
}

}
