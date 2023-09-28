package com.epam.rd.autotasks.springemployeecatalog.service;

import com.epam.rd.autotasks.springemployeecatalog.domain.Employee;
import com.epam.rd.autotasks.springemployeecatalog.repository.EmployeeRepository;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getEmployeeById(Integer id, boolean fullChain, Integer page, Integer size, String sorting) throws NotFoundException {
        List<Employee> limitedEmployees = new ArrayList<>();
        List<Employee> employees = new ArrayList<>();

        if (id == null) {
            employees = (List<Employee>) employeeRepository.findAll();

        } else if (fullChain) {
            Employee employee = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee with that id does not exist"));

            employees.add(employee);

            while (employee.getManager() != null) {
                employee = employee.getManager();
                employees.add(employee);
            }
        } else {
            Employee employee = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee with that id does not exist"));

            employees.add(employee);
        }

        if (page != null && size != null) {
            for (int i = page * size; i < size * (page + 1); i++) {
                limitedEmployees.add(employees.get(i));
            }
        }

        if (sorting != null) {
            switch (sorting) {
                case "lastName":
                    limitedEmployees.sort(Comparator.comparing(employee -> employee.getFullName().getLastName()));
                    break;
                case "hired":
                    limitedEmployees.sort(Comparator.comparing(Employee::getHired));
                    break;
                case "position":
                    limitedEmployees.sort(Comparator.comparing(Employee::getPosition));
                    break;
                case "salary":
                    limitedEmployees.sort(Comparator.comparing(Employee::getSalary));
                    break;
            }
        }
        return limitedEmployees;
    }

    public List<Employee> getSubordinates(Integer managerId, Integer page, Integer size, String sorting) {
        List<Employee> employees = employeeRepository.getAllSubordinates(managerId);

        List<Employee> limitedEmployees = new ArrayList<>();

        if (page != null && size != null) {
            for (int i = page * size; i < size * (page + 1); i++) {
                limitedEmployees.add(employees.get(i));
            }
        }

        if (sorting != null) {
            switch (sorting) {
                case "lastName":
                    limitedEmployees.sort(Comparator.comparing(employee -> employee.getFullName().getLastName()));
                    break;
                case "hired":
                    limitedEmployees.sort(Comparator.comparing(Employee::getHired));
                    break;
                case "position":
                    limitedEmployees.sort(Comparator.comparing(Employee::getPosition));
                    break;
                case "salary":
                    limitedEmployees.sort(Comparator.comparing(Employee::getSalary));
                    break;
            }
        }
        return limitedEmployees;
    }

    public List<Employee> getEmployeesByDepartment(String department, Integer page, Integer size, String sorting) {
        List<Employee> employees;

        List<Employee> limitedEmployees = new ArrayList<>();

        if (Character.isDigit(department.charAt(0))) {
            employees = employeeRepository.getEmployeeByDepartment_Id(Integer.parseInt(department));
        } else {
            employees = employeeRepository.getEmployeeByDepartmentName(department);
        }

        if (page != null && size != null) {
            for (int i = page * size; i < size * (page + 1); i++) {
                limitedEmployees.add(employees.get(i));
            }
        }

        if (sorting != null) {
            switch (sorting) {
                case "lastName":
                    limitedEmployees.sort(Comparator.comparing(employee -> employee.getFullName().getLastName()));
                    break;
                case "hired":
                    limitedEmployees.sort(Comparator.comparing(Employee::getHired));
                    break;
                case "position":
                    limitedEmployees.sort(Comparator.comparing(Employee::getPosition));
                    break;
                case "salary":
                    limitedEmployees.sort(Comparator.comparing(Employee::getSalary));
                    break;
            }
        }
        return limitedEmployees;
    }
}
