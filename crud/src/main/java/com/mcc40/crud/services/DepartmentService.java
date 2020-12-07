/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Department;
import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Job;
import com.mcc40.crud.repositories.DepartmentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mochamad Yusuf
 */
@Service
public class DepartmentService {

    @Autowired
    DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getByIdDepartment(int id) {
        return departmentRepository.findById(id).get();
    }

    public String saveDepartment(Department department) {
        String result = null;
        Optional<Department> optionalDepartment = departmentRepository.findById(department.getId());
        try {
            if (optionalDepartment.isPresent() == false) {
                departmentRepository.save(department);
                result = "Inserted";
            } else if (optionalDepartment.get().equals(true)) {
                Department oldDepartment = optionalDepartment.get();
                oldDepartment.setName(department.getName());
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        return result;
    }

    public boolean deleteById(int id) {
        departmentRepository.deleteById(id);
        return !departmentRepository.findById(id).isPresent();
    }

    public void getDepartmentNameAndJobTitle() {
        List<Department> departments = departmentRepository.findAll();
        for (Department department : departments) {
            List<Employee> employeeList = department.getEmployeeList();
            for (Employee employee : employeeList) {
                System.out.print(department.getName()+ "  |   ");
                System.out.println(employee.getJob().getTitle());
            }
        }
    }
}
