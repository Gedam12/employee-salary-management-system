
package com.incubyte.salarymanagement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            String name,
            String email,
            String department,
            Pageable pageable
    );

    
@Query("""
    SELECT e FROM Employee e
    WHERE (:department = 'all' OR e.department = :department)
      AND (
          :search = ''
          OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(e.department) LIKE LOWER(CONCAT('%', :search, '%'))
      )
    """)
Page<Employee> searchEmployees(
        @Param("search") String search,
        @Param("department") String department,
        Pageable pageable
);


@Query("SELECT COALESCE(SUM(e.salary), 0) FROM Employee e")
BigDecimal getTotalSalary();


}
