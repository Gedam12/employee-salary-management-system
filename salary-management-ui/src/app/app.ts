
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Employee, EmployeeService } from './employee.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  private employeeService = inject(EmployeeService);
  private cdr = inject(ChangeDetectorRef);

  // Employee list
  employees: Employee[] = [];

  // Search, filtering and sorting
  searchTerm = '';
  salarySort = 'none';
  selectedDepartment = 'all';

  // Server-side pagination
  currentPage = 1;
  pageSize = 10;

  serverTotalEmployees = 0;
  serverTotalPages = 1;

  // Organization-wide salary statistics
  serverTotalSalary = 0;
  serverAverageSalary = 0;

  // Loading and error states
  loading = true;
  errorMessage = '';

  // Add Employee form
  newEmployee = {
    name: '',
    email: '',
    department: '',
    salary: 0
  };

  saving = false;
  formError = '';
  formSuccess = '';

  // Edit Employee form
  editingEmployeeId: number | null = null;

  editEmployee = {
    name: '',
    email: '',
    department: '',
    salary: 0
  };

  editSaving = false;
  editError = '';

  // Available departments
  get departments(): string[] {
    return [...new Set(
      this.employees.map(employee => employee.department)
    )].sort();
  }

  // Employee count for current search/filter
  get totalEmployees(): number {
    return this.serverTotalEmployees;
  }

  // Organization-wide total salary
  get totalSalary(): number {
    return this.serverTotalSalary;
  }

  // Organization-wide average salary
  get averageSalary(): number {
    return this.serverAverageSalary;
  }

  // Load data when application starts
  ngOnInit(): void {
    this.loadEmployees();
  }

  // Fetch salary statistics from backend
  loadSalarySummary(): void {
    this.employeeService.getSalarySummary().subscribe({
      next: (data) => {
        this.serverTotalSalary = data.totalSalary;
        this.serverAverageSalary = data.averageSalary;
      },
      error: () => {
        this.errorMessage =
          'Unable to load salary summary.';
      }
    });
  }

  // Existing client-side filtering helper
  get filteredEmployees(): Employee[] {
    const search = this.searchTerm.toLowerCase().trim();

    let result = this.employees.filter(employee => {
      const matchesSearch =
        employee.name.toLowerCase().includes(search) ||
        employee.email.toLowerCase().includes(search) ||
        employee.department.toLowerCase().includes(search);

      const matchesDepartment =
        this.selectedDepartment === 'all' ||
        employee.department === this.selectedDepartment;

      return matchesSearch && matchesDepartment;
    });

    if (this.salarySort === 'high') {
      result = [...result].sort(
        (a, b) => b.salary - a.salary
      );
    } else if (this.salarySort === 'low') {
      result = [...result].sort(
        (a, b) => a.salary - b.salary
      );
    }

    return result;
  }

  // Total pages returned by backend
  get totalPages(): number {
    return Math.max(1, this.serverTotalPages);
  }

  get safeCurrentPage(): number {
    return Math.min(
      Math.max(this.currentPage, 1),
      this.totalPages
    );
  }

  // Employees are already paginated by backend
  get paginatedEmployees(): Employee[] {
    return this.employees;
  }

  // Load employees using server-side pagination
  
loadEmployees(): void {
  this.loading = true;
  this.errorMessage = '';
  this.cdr.detectChanges();

  this.employeeService.getEmployeesPaged(
    this.currentPage - 1,
    this.pageSize,
    this.searchTerm,
    this.selectedDepartment,
    this.salarySort
  ).subscribe({
    next: (data) => {
      this.employees = data.content;
      this.serverTotalEmployees = data.totalElements;
      this.serverTotalPages = data.totalPages;

      this.loading = false;
      this.cdr.detectChanges();

      this.loadSalarySummary();
    },
    error: () => {
      this.errorMessage =
        'Unable to load employees. Please try again.';

      this.loading = false;
      this.cdr.detectChanges();
    }
  });
}


  // Add a new employee
  saveEmployee(): void {
    this.formError = '';
    this.formSuccess = '';

    if (
      !this.newEmployee.name.trim() ||
      !this.newEmployee.email.trim() ||
      !this.newEmployee.department.trim() ||
      !Number.isFinite(this.newEmployee.salary) ||
      this.newEmployee.salary <= 0
    ) {
      this.formError =
        'Please enter valid details in all fields.';
      return;
    }

    this.saving = true;

    this.employeeService.createEmployee(
      this.newEmployee
    ).subscribe({
      next: () => {
        this.formSuccess =
          'Employee added successfully!';

        this.newEmployee = {
          name: '',
          email: '',
          department: '',
          salary: 0
        };

        this.saving = false;
        this.loadEmployees();
      },
      error: (error) => {
        this.formError =
          error.error?.message ||
          'Unable to save employee.';

        this.saving = false;
      }
    });
  }

  // Open Edit Employee form
  startEditing(employee: Employee): void {
    this.editingEmployeeId = employee.id;

    this.editEmployee = {
      name: employee.name,
      email: employee.email,
      department: employee.department,
      salary: employee.salary
    };

    this.editError = '';
    this.formSuccess = '';
  }

  // Save edited employee
  saveEditedEmployee(): void {
    if (this.editingEmployeeId === null) {
      return;
    }

    this.editError = '';

    if (
      !this.editEmployee.name.trim() ||
      !this.editEmployee.email.trim() ||
      !this.editEmployee.department.trim() ||
      !Number.isFinite(this.editEmployee.salary) ||
      this.editEmployee.salary <= 0
    ) {
      this.editError =
        'Please enter valid employee details.';
      return;
    }

    this.editSaving = true;

    this.employeeService.updateEmployee(
      this.editingEmployeeId,
      this.editEmployee
    ).subscribe({
      next: () => {
        this.editSaving = false;
        this.editingEmployeeId = null;

        this.formSuccess =
          'Employee updated successfully!';

        this.loadEmployees();
      },
      error: (error) => {
        this.editSaving = false;

        this.editError =
          error.error?.message ||
          'Unable to update employee.';
      }
    });
  }

  // Cancel editing
  cancelEditing(): void {
    this.editingEmployeeId = null;
    this.editError = '';
  }

  // Delete employee with confirmation
  deleteEmployee(employee: Employee): void {
    const confirmed = window.confirm(
      `Are you sure you want to delete ${employee.name}?`
    );

    if (!confirmed) {
      return;
    }

    this.formError = '';
    this.formSuccess = '';

    this.employeeService.deleteEmployee(
      employee.id
    ).subscribe({
      next: () => {
        if (this.editingEmployeeId === employee.id) {
          this.cancelEditing();
        }

        this.formSuccess =
          `${employee.name} deleted successfully!`;

        this.loadEmployees();
      },
      error: (error) => {
        this.formError =
          error.error?.message ||
          'Unable to delete employee.';
      }
    });
  }
}
