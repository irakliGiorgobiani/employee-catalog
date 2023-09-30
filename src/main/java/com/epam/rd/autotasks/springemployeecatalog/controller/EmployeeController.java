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

    @GetMapping(path = {"employees", "employees/{employee_id}"})
    public List<Employee> getEmployees(@PathVariable(name = "employee_id", required = false) Long employee_id,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size,
                                          @RequestParam(required = false) String sorting,
                                          @RequestParam(required = false) boolean fullChain) throws NotFoundException {

        if (employee_id == null) {
            return employeeService.getAllEmployees(page, size, sorting);
        } else if (!fullChain) {
            return employeeService.getEmployeeById(employee_id);
        } else {
            return employeeService.getFullChain(employee_id, page, size, sorting);
        }
    }

    @GetMapping("employee/by_manager/{managerId}")
    public List<Employee> getSubordinates(@PathVariable(value = "managerId", required = false) Long managerId, @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size, @RequestParam(required = false) String sorting) throws NotFoundException {
        return employeeService.getSubordinates(managerId, page, size, sorting);
    }

    @GetMapping("employee/by_department/{department}")
    public List<Employee> getEmployeesByDepartment(@PathVariable(value = "department", required = false) String department,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer size,
                                                   @RequestParam(required = false) String sorting) throws NotFoundException {
        return employeeService.getEmployeesByDepartment(department, page, size, sorting);
    }
}
