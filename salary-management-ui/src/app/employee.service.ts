import { retry, timer } from 'rxjs';
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Employee data model
export interface Employee {
  id: number;
  name: string;
  email: string;
  department: string;
  salary: number;
}

// Server-side pagination response
export interface EmployeePage {
  content: Employee[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

// Organization-wide salary summary
export interface SalarySummary {
  totalEmployees: number;
  totalSalary: number;
  averageSalary: number;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private http = inject(HttpClient);

  private apiUrl = '/api/employees';

  // Get all employees (existing endpoint)
  getEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.apiUrl);
  }

  // Get paginated employees with search, filtering and sorting
  getEmployeesPaged(
  page: number = 0,
  size: number = 10,
  search: string = '',
  department: string = 'all',
  salarySort: string = 'none'
): Observable<EmployeePage> {

  return this.http.get<EmployeePage>(
    `${this.apiUrl}/paged`,
    {
      params: {
        page: page.toString(),
        size: size.toString(),
        search,
        department,
        salarySort
      }
    }
  ).pipe(
    retry({
      count: 8,
      delay: (error, retryCount) => {
        // Retry temporary backend failures only
        if (
          error.status !== 0 &&
          error.status !== 502 &&
          error.status !== 503 &&
          error.status !== 504
        ) {
          throw error;
        }

        return timer(10000);
      }
    })
  );
}

  // Get organization-wide salary statistics
  getSalarySummary(): Observable<SalarySummary> {
    return this.http.get<SalarySummary>(
      `${this.apiUrl}/summary`
    );
  }

  // Create a new employee
  createEmployee(
    employee: Omit<Employee, 'id'>
  ): Observable<Employee> {

    return this.http.post<Employee>(
      this.apiUrl,
      employee
    );
  }

  // Update employee information
  updateEmployee(
    id: number,
    employee: Omit<Employee, 'id'>
  ): Observable<Employee> {

    return this.http.put<Employee>(
      `${this.apiUrl}/${id}`,
      employee
    );
  }

  // Delete an employee
  deleteEmployee(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}
