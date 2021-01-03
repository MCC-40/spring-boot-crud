/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Department;
import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.repositories.DepartmentRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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

    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    public Department getById(int id) {
        Optional<Department> department = departmentRepository.findById(id);
        if (department.isPresent()) {
            return department.get();
        } else {
            return null;
        }
    }

    public List<Department> getByKeyword(String keyword) {
        List<Department> departmentList = new ArrayList<>();
        List<Map<String, Object>> mapList = new ArrayList<>();

        if (keyword != null) {
            departmentList = departmentRepository.findAll();
            departmentList = departmentList.stream().filter(d
                    -> d.getId().toString().contains(keyword)
                    || d.getName().toString().contains(keyword)
            ).collect(Collectors.toList());
        } else {
            departmentList = departmentRepository.findAll();
        }

        for (Department department : departmentList) {
            mapList.add(department.getJsonProperties());
        }
        return departmentList;
    }

    public String insert(Department department) {
        String result = "Error";
        Optional<Department> optionalDepartment = departmentRepository.findById(department.getId());
        try {
            if (!optionalDepartment.isPresent()) {
                departmentRepository.save(department);
                result = "Inserted";
            } else {
                result = "Data already exist";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        return result;
    }

    public String update(Department department) {
        String result = "Error";
        Optional<Department> optionalDepartment = departmentRepository.findById(department.getId());
        try {
            if (!optionalDepartment.isPresent()) {
                result = "Data not exist";
            } else {
                Department oldDepartment = optionalDepartment.get();
                if (department.getName() != null) {
                    oldDepartment.setName(department.getName());
                }
                if (department.getManager() != null) {
                    oldDepartment.setManager(department.getManager());
                }
                if (department.getLocation() != null) {
                    oldDepartment.setLocation(department.getLocation());
                }
                departmentRepository.save(oldDepartment);
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
                System.out.print(department.getName() + "  |   ");
                System.out.println(employee.getJob().getTitle());
            }
        }
    }
}
