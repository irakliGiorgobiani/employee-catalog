package com.epam.rd.autotasks.springemployeecatalog.repository;

import com.epam.rd.autotasks.springemployeecatalog.domain.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
    @Query(value = "SELECT * FROM EMPLOYEE WHERE MANAGER = :id", nativeQuery = true)
    List<Employee> getAllSubordinates(Integer id);

    @Query(value = "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = :department", nativeQuery = true)
    List<Employee> getEmployeeByDepartment_Id(Integer department);

    @Query(value = "SELECT * FROM EMPLOYEE JOIN DEPARTMENT WHERE EMPLOYEE.ID = DEPARTMENT.ID AND DEPARTMENT.NAME = :departmentName", nativeQuery = true)
    List<Employee> getEmployeeByDepartmentName(String departmentName);
}


