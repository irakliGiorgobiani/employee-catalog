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

    @GetMapping("employees/{employee_id}")
    public List<Employee> getEmployees(@PathVariable("employee_id") Integer employee_id, @RequestParam boolean full_chain,
    @RequestParam Integer page, @RequestParam Integer size, @RequestParam String sorting) throws NotFoundException {
        if (employee_id == null) {
            return employeeService.getEmployeeById(null, false, null, null, null);
        } else if (!full_chain) {
            return employeeService.getEmployeeById(employee_id, false, page, size, sorting);
        } else {
            return employeeService.getEmployeeById(employee_id, true, page, size, sorting);
        }
    }

    @GetMapping("employee/by_manager/{managerId}")
    public List<Employee> getSubordinates(@PathVariable("managerId") Integer managerId, @RequestParam Integer page,
                                          @RequestParam Integer size, @RequestParam String sorting) {
        return employeeService.getSubordinates(managerId, page, size, sorting);
    }

    @GetMapping("employee/by_department/{department}")
    public List<Employee> getEmployeesByDepartment(@PathVariable("department") String department,
                                                   @RequestParam Integer page,
                                                   @RequestParam Integer size,
                                                   @RequestParam String sorting) {
        return employeeService.getEmployeesByDepartment(department, page, size, sorting);
    }
}
