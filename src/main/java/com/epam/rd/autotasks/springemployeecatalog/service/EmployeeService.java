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

    private Employee mapEmployeeEntityToEmployeeNoChain(EmployeeEntity employeeEntity) throws NotFoundException {
        if (employeeEntity.getDepartment() == null) {
            return new Employee(employeeEntity.getId(), new FullName(employeeEntity.getFirstName(), employeeEntity.getLastName(), employeeEntity.getMiddleName()),
                    Position.valueOf(employeeEntity.getPosition()), employeeEntity.getHire(), employeeEntity.getSalary(), null, null);
        } else if (employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getDepartment() == null) {
            return new Employee(employeeEntity.getId(), new FullName(employeeEntity.getFirstName(), employeeEntity.getLastName(), employeeEntity.getMiddleName()),
                    Position.valueOf(employeeEntity.getPosition()), employeeEntity.getHire(), employeeEntity.getSalary(),
                    new Employee(employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getId(),
                            new FullName(employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getFirstName(),
                                    employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getLastName(),
                                    employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getMiddleName()),
                            Position.valueOf(employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getPosition()),
                            employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getHire(),
                            employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getSalary(), null, null),
                    new Department(departmentRepository.findById(employeeEntity.getDepartment()).orElseThrow(() -> new NotFoundException("Department was not found")).getId(),
                            departmentRepository.findById(employeeEntity.getDepartment()).orElseThrow(() -> new NotFoundException("Department was not found")).getName(),
                            departmentRepository.findById(employeeEntity.getDepartment()).orElseThrow(() -> new NotFoundException("Department was not found")).getLocation()));
        } else return new Employee(employeeEntity.getId(), new FullName(employeeEntity.getFirstName(), employeeEntity.getLastName(), employeeEntity.getMiddleName()),
                Position.valueOf(employeeEntity.getPosition()), employeeEntity.getHire(), employeeEntity.getSalary(),
                new Employee(employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getId(),
                        new FullName(employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getFirstName(),
                                employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getLastName(),
                                employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getMiddleName()),
                        Position.valueOf(employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getPosition()),
                        employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getHire(),
                        employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found")).getSalary(), null,
                        new Department(departmentRepository.findById(employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found"))
                                .getDepartment()).orElseThrow(() -> new NotFoundException("Department was not found")).getId(),
                                departmentRepository.findById(employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found"))
                                        .getDepartment()).orElseThrow(() -> new NotFoundException("Department was not found")).getName(),
                                departmentRepository.findById(employeeRepository.findById(employeeEntity.getManager()).orElseThrow(() -> new NotFoundException("Employee was not found"))
                                        .getDepartment()).orElseThrow(() -> new NotFoundException("Department was not found")).getLocation())),
                new Department(departmentRepository.findById(employeeEntity.getDepartment()).orElseThrow(() -> new NotFoundException("Department was not found")).getId(),
                        departmentRepository.findById(employeeEntity.getDepartment()).orElseThrow(() -> new NotFoundException("Department was not found")).getName(),
                        departmentRepository.findById(employeeEntity.getDepartment()).orElseThrow(() -> new NotFoundException("Department was not found")).getLocation()));
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

        if (sorting != null) {
            switch (sorting) {
                case "lastName":
                    employees.sort(Comparator.comparing(employee -> employee.getFullName().getLastName()));
                    break;
                case "hired":
                    employees.sort(Comparator.comparing(Employee::getHired));
                    break;
                case "position":
                    employees.sort(Comparator.comparing(employee -> employee.getPosition().toString()));
                    break;
                case "salary":
                    employees.sort(Comparator.comparing(Employee::getSalary));
                    break;
            }
        }

        for (Employee employee:
             employees) {
            System.out.println(employee);
        }

        if (size != null) {
            if (page == null) {
                if (size >= employees.size()) {
                    limitedEmployees = employees;
                } else {
                    for (int i = 0; i < size + 1; i++) {
                        limitedEmployees.add(employees.get(i));
                    }
                }
            } else {
                if (size >= employees.size() && page == 0) {
                    limitedEmployees = employees;
                } else if (size >= employees.size()) {
                    limitedEmployees = new ArrayList<>();
                } else if ((size * (page + 1)) <= employees.size()) {
                    for (int i = page * size; i < size * (page + 1); i++) {
                        limitedEmployees.add(employees.get(i));
                    }
                } else if ((size * (page + 1)) > employees.size()) {
                    limitedEmployees = new ArrayList<>();
                }
            }
        } else limitedEmployees = employees;

        return limitedEmployees;
    }

    public List<Employee> getAllEmployees(Integer page, Integer size, String sorting) throws NotFoundException {
        List<EmployeeEntity> allEmployeeEntities = employeeRepository.findAll();

        List<Employee> allEmployees = new ArrayList<>();

        for (EmployeeEntity entity : allEmployeeEntities) {
            allEmployees.add(mapEmployeeEntityToEmployeeNoChain(entity));
        }

        return paging(allEmployees, page, size, sorting);
    }

    public Employee getEmployeeById(Long id) throws NotFoundException {
        EmployeeEntity employeeEntity = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee with that id was not found"));

        return mapEmployeeEntityToEmployeeNoChain(employeeEntity);
    }

    public Employee getFullChain(Long id) throws NotFoundException {
        EmployeeEntity employeeEntity = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee with that id was not found"));

        return mapEmployeeEntityToEmployee(employeeEntity);
    }

    public List<Employee> getSubordinates(Long managerId, Integer page, Integer size, String sorting) throws NotFoundException {
        if (employeeRepository.findById(managerId).isEmpty()) {
            throw new NotFoundException("Employee with that id was not found");
        }

        List<EmployeeEntity> employeeEntities = employeeRepository.getAllSubordinates(managerId);

        List<Employee> employees = new ArrayList<>();

        for (EmployeeEntity employee : employeeEntities) {
            employees.add(mapEmployeeEntityToEmployeeNoChain(employee));
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
            employees.add(mapEmployeeEntityToEmployeeNoChain(employee));
        }

        return paging(employees, page, size, sorting);
    }
}
