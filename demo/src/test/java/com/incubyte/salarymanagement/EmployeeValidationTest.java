
package com.incubyte.salarymanagement;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeValidationTest {

    @Test
    void shouldRejectNegativeSalary() {
        try (ValidatorFactory factory =
                     Validation.buildDefaultValidatorFactory()) {

            Validator validator = factory.getValidator();

            Employee employee = new Employee();
            employee.setName("Test Employee");
            employee.setEmail("test@example.com");
            employee.setDepartment("Engineering");
            employee.setSalary(new BigDecimal("-5000"));

            var violations = validator.validate(employee);

            assertTrue(
                violations.stream().anyMatch(
                    violation -> violation.getPropertyPath()
                        .toString().equals("salary")
                )
            );
        }
    }

    
@Test
void shouldRejectInvalidEmail() {
    try (ValidatorFactory factory =
                 Validation.buildDefaultValidatorFactory()) {

        Validator validator = factory.getValidator();

        Employee employee = new Employee();
        employee.setName("Alex Johnson");
        employee.setEmail("invalid-email");
        employee.setDepartment("Engineering");
        employee.setSalary(new BigDecimal("75000"));

        var violations = validator.validate(employee);

        assertTrue(
            violations.stream().anyMatch(
                violation -> violation.getPropertyPath()
                    .toString().equals("email")
            )
        );
    }
}


@Test
void shouldRejectBlankEmployeeName() {
    try (ValidatorFactory factory =
                 Validation.buildDefaultValidatorFactory()) {

        Validator validator = factory.getValidator();

        Employee employee = new Employee();
        employee.setName("");
        employee.setEmail("test@example.com");
        employee.setDepartment("Engineering");
        employee.setSalary(new BigDecimal("75000"));

        var violations = validator.validate(employee);

        assertTrue(
            violations.stream().anyMatch(
                violation -> violation.getPropertyPath()
                    .toString().equals("name")
            )
        );
    }
}


}
