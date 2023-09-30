package com.epam.rd.autotasks.springemployeecatalog.service;

import com.epam.rd.autotasks.springemployeecatalog.domain.Department;
import com.epam.rd.autotasks.springemployeecatalog.domain.Employee;
import com.epam.rd.autotasks.springemployeecatalog.domain.FullName;
import com.epam.rd.autotasks.springemployeecatalog.domain.Position;
import com.epam.rd.autotasks.springemployeecatalog.entity.DepartmentEntity;
import com.epam.rd.autotasks.springemployeecatalog.entity.EmployeeEntity;
import com.epam.rd.autotasks.springemployeecatalog.repository.DepartmentRepository;
import com.epam.rd.autotasks.springemployeecatalog.repository.EmployeeRepository;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    private Employee mapEmployeeEntityToEmployee(EmployeeEntity employeeEntity) {
        if (employeeEntity.getManager() == null) {
            return new Employee(employeeEntity.getId(), new FullName(employeeEntity.getFirstName(), employeeEntity.getLastName(), employeeEntity.getMiddleName()),
                    Position.valueOf(employeeEntity.getPosition()), employeeEntity.getHire(), employeeEntity.getSalary(), null, null);
        }

        List<EmployeeEntity> employeeEntities = new ArrayList<>();

        while (employeeEntity != null) {
            employeeEntities.add(employeeEntity);

            if (employeeEntity.getManager() == null) {
                break;
            }

            employeeEntity = employeeRepository.findById(employeeEntity.getManager()).orElse(null);
        }

        List<Employee> employees = new ArrayList<>();

        for (int i = 0; i < employeeEntities.size(); i++) {
            EmployeeEntity employee = employeeEntities.get(i);

            if (i == employeeEntities.size() - 1) {
                employees.add(new Employee(employeeEntity.getId(), new FullName(employeeEntity.getFirstName(), employeeEntity.getLastName(), employeeEntity.getMiddleName()),
                        Position.valueOf(employeeEntity.getPosition()), employeeEntity.getHire(), employeeEntity.getSalary(), null, null));
            } else employees.add(new Employee(employee.getId(), new FullName(employee.getFirstName(), employee.getLastName(), employee.getMiddleName()),
                    Position.valueOf(employee.getPosition()), employee.getHire(), employee.getSalary(), null, new Department(employee.getDepartment(),
                    departmentRepository.findById(employee.getDepartment()).orElse(null).getName(), departmentRepository.findById(employee.getDepartment()).orElse(null).getLocation())));
        }

        List<Employee> connectedEmployees = new ArrayList<>();

        connectedEmployees.add(employees.get(employees.size() - 1));

        for (int i = employees.size() - 2; i >= 0; i--) {
            Employee employee = employees.get(i);
            Employee manager = connectedEmployees.get(connectedEmployees.size() - 1);

            connectedEmployees.add(new Employee(employee.getId(), employee.getFullName(), employee.getPosition(), employee.getHired(), employee.getSalary(), manager, employee.getDepartment()));
        }

        return connectedEmployees.get(connectedEmployees.size() - 1);
    }

    private List<Employee> paging(List<Employee> employees, Integer page, Integer size, String sorting) {
        List<Employee> limitedEmployees = new ArrayList<>();

        if (page != null && size != null) {
            for (int i = page * size; i < size * (page + 1); i++) {
                limitedEmployees.add(employees.get(i));
            }
        } else limitedEmployees = employees;

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

    public List<Employee> getAllEmployees(Integer page, Integer size, String sorting) {
        List<EmployeeEntity> allEmployeeEntities = employeeRepository.findAll();

        List<Employee> allEmployees = new ArrayList<>();

        for (EmployeeEntity entity : allEmployeeEntities) {
            allEmployees.add(mapEmployeeEntityToEmployee(entity));
        }

        return paging(allEmployees, page, size, sorting);
    }

    public List<Employee> getEmployeeById(Long id) throws NotFoundException {
        EmployeeEntity employeeEntity = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee with that id was not found"));

        List<Employee> employees = new ArrayList<>();
        employees.add(mapEmployeeEntityToEmployee(employeeEntity));

        return employees;
    }

    public List<Employee> getFullChain(Long id, Integer page, Integer size, String sorting) throws NotFoundException {
        EmployeeEntity employeeEntity = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee with that id was not found"));

        Employee employee = mapEmployeeEntityToEmployee(employeeEntity);

        List<Employee> employeeChain = new ArrayList<>();

        while (employee != null) {
            employeeChain.add(employee);

            employee = employee.getManager();
        }

        return paging(employeeChain, page, size, sorting);
    }

    public List<Employee> getSubordinates(Long managerId, Integer page, Integer size, String sorting) throws NotFoundException {
        if (employeeRepository.findById(managerId).isEmpty()) {
            throw new NotFoundException("Employee with that id was not found");
        }

        List<EmployeeEntity> employeeEntities = employeeRepository.getAllSubordinates(managerId);

        List<Employee> employees = new ArrayList<>();

        for (EmployeeEntity employee : employeeEntities) {
            employees.add(mapEmployeeEntityToEmployee(employee));
        }

        return paging(employees, page, size, sorting);
    }

    public List<Employee> getEmployeesByDepartment(String department, Integer page, Integer size, String sorting) throws NotFoundException {
        List<EmployeeEntity> employeeEntities;

        if (Character.isDigit(department.charAt(0))) {
            if (!employeeRepository.getEmployeeByDepartmentId(Long.parseLong(department)).isEmpty()) {
                employeeEntities = employeeRepository.getEmployeeByDepartmentId(Long.parseLong(department));
            } else throw new NotFoundException("Employees were not found");
        } else {
            DepartmentEntity departmentEntity = departmentRepository.findByDepartmentName(department);

            if (departmentEntity != null) {
                employeeEntities = employeeRepository.getEmployeeByDepartmentId(departmentEntity.getId());
            } else throw new NotFoundException("Employees were not found");
        }

        List<Employee> employees = new ArrayList<>();

        for (EmployeeEntity employee : employeeEntities) {
            employees.add(mapEmployeeEntityToEmployee(employee));
        }

        return paging(employees, page, size, sorting);
    }
}
