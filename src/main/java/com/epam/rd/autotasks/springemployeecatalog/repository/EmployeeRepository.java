package com.epam.rd.autotasks.springemployeecatalog.repository;


import com.epam.rd.autotasks.springemployeecatalog.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    @Query(value = "SELECT * FROM EMPLOYEE WHERE MANAGER = :id", nativeQuery = true)
    List<EmployeeEntity> getAllSubordinates(Long id);

    @Query(value = "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = :department", nativeQuery = true)
    List<EmployeeEntity> getEmployeeByDepartmentId(Long department);
}


