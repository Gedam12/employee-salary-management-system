
package com.incubyte.salarymanagement;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Get all employees
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Server-side pagination, search, filtering, and sorting
    @GetMapping("/paged")
    public Page<Employee> getEmployeesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "all") String department,
            @RequestParam(defaultValue = "none") String salarySort) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be non-negative and size must be between 1 and 100"
            );
        }

        Sort sort;

        if ("high".equals(salarySort)) {
            sort = Sort.by(
                    Sort.Order.desc("salary"),
                    Sort.Order.asc("id")
            );
        } else if ("low".equals(salarySort)) {
            sort = Sort.by(
                    Sort.Order.asc("salary"),
                    Sort.Order.asc("id")
            );
        } else if ("none".equals(salarySort)) {
            sort = Sort.by("id").ascending();
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid salary sort option"
            );
        }

        PageRequest pageable = PageRequest.of(page, size, sort);

        return employeeRepository.searchEmployees(
                search.trim(),
                department,
                pageable
        );
    }

    // Organization-wide salary summary
    @GetMapping("/summary")
    public Map<String, Object> getSalarySummary() {

        long totalEmployees = employeeRepository.count();

        BigDecimal totalSalary = employeeRepository.getTotalSalary();

        BigDecimal averageSalary = totalEmployees == 0
                ? BigDecimal.ZERO
                : totalSalary.divide(
                        BigDecimal.valueOf(totalEmployees),
                        2,
                        RoundingMode.HALF_UP
                );

        return Map.<String, Object>of(
                "totalEmployees", totalEmployees,
                "totalSalary", totalSalary,
                "averageSalary", averageSalary
        );
    }

    // Get employee by ID
    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found"
                ));
    }

    // Create employee
    @PostMapping
    public Employee createEmployee(
            @Valid @RequestBody Employee employee) {

        return employeeRepository.save(employee);
    }

    // Update employee
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

    // Delete employee
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

    // Increase employee salary by percentage
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
                .divide(
                        new BigDecimal("100"),
                        2,
                        RoundingMode.HALF_UP
                );

        employee.setSalary(
                employee.getSalary().add(increase)
        );

        return employeeRepository.save(employee);
    }
}
