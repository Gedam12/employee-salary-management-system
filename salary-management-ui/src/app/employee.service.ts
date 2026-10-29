
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Employee {
  id: number;
  name: string;
  email: string;
  department: string;
  salary: number;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private http = inject(HttpClient);

  private apiUrl = '/api/employees';

  getEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.apiUrl);
  }

  
  createEmployee(employee: Omit<Employee, 'id'>): Observable<Employee> {
    return this.http.post<Employee>(this.apiUrl, employee);
  }

  
updateEmployee(
  id: number,
  employee: Omit<Employee, 'id'>
): Observable<Employee> {
  return this.http.put<Employee>(
    `${this.apiUrl}/${id}`,
    employee
  );
}


deleteEmployee(id: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl}/${id}`);
}



}
