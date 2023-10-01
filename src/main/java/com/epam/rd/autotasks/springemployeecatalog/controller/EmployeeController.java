package com.epam.rd.autotasks.springemployeecatalog.controller;

import com.epam.rd.autotasks.springemployeecatalog.domain.Employee;
import com.epam.rd.autotasks.springemployeecatalog.service.EmployeeService;
import javassist.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("employees")
    public List<Employee> getEmployees(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size,
                                          @RequestParam(required = false) String sort) throws NotFoundException {
        return employeeService.getAllEmployees(page, size, sort);
    }

    @GetMapping("employees/{employee_id}")
    public Employee getEmployee(@PathVariable("employee_id") Long employee_id, @RequestParam(required = false) boolean full_chain) throws NotFoundException {
        if (full_chain) {
            return employeeService.getFullChain(employee_id);
        } else return employeeService.getEmployeeById(employee_id);
    }

    @GetMapping("employees/by_manager/{managerId}")
    public List<Employee> getSubordinates(@PathVariable(value = "managerId", required = false) Long managerId, @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) throws NotFoundException {
        return employeeService.getSubordinates(managerId, page, size, sort);
    }

    @GetMapping("employees/by_department/{department}")
    public List<Employee> getEmployeesByDepartment(@PathVariable(value = "department", required = false) String department,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer size,
                                                   @RequestParam(required = false) String sort) throws NotFoundException {
        return employeeService.getEmployeesByDepartment(department, page, size, sort);
    }
}
