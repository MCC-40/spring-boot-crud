/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.repositories;

import com.mcc40.crud.entities.Employee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Yoshua
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>{
        Optional<Employee> findByEmail(String email);
        
        @Query(value = "SELECT * FROM employees E JOIN employees M ON E.manager = M.id GROUP BY M.id", nativeQuery = true)
        public List<Employee> getManagers();
}
