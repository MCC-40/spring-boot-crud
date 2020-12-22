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

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getById(int id) {
        return departmentRepository.findById(id).get();
    }

    public List<Map<String, Object>> searchByKeywordOrId(String keyword, Integer id) {
        List<Department> departmentList = new ArrayList<>();
        List<Map<String, Object>> mapList = new ArrayList<>();

        if (id != null) {
            Optional<Department> optionalDepartment = departmentRepository.findById(id);
            if (optionalDepartment.isPresent()) {
                departmentList.add(optionalDepartment.get());
            }
        } else if (keyword != null) {
            departmentList = departmentRepository.findAll();
            departmentList = departmentList.stream().filter(d
                    -> d.getId().toString().contains(keyword)
                    || d.getName().toString().contains(keyword)
            ).collect(Collectors.toList());
        } else {
            departmentList = departmentRepository.findAll();
        }

        for (Department department : departmentList) {
            Map map = new HashMap();
            map.put("id", department.getId());
            map.put("name", department.getName());
            if (department.getManager() != null) {
                map.put("managerId", department.getManager().getId());
                map.put("manager", department.getManager().getFirstName() + " " + department.getManager().getLastName());
            } else {
                map.put("managerId", null);
                map.put("manager", "No manager");
            }
            map.put("locationId", department.getLocation().getId());
            map.put("location", department.getLocation().getStreetAddress());
            mapList.add(map);
        }
        return mapList;
    }

    public String saveDepartment(Department department) {
        String result = "Error";
        Optional<Department> optionalDepartment = departmentRepository.findById(department.getId());
        try {
            if (!optionalDepartment.isPresent()) {
                departmentRepository.save(department);
                result = "Inserted";
            } else {
                Department oldDepartment = optionalDepartment.get();
                oldDepartment.setName(department.getName());
                oldDepartment.setManager(department.getManager());
                oldDepartment.setLocation(department.getLocation());
                departmentRepository.save(oldDepartment);
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        return result;
    }

    public String putDepartment(Department department) {
        String result = "Error";
        Optional<Department> optionalDepartment = departmentRepository.findById(department.getId());
        try {
            if (!optionalDepartment.isPresent()) {
                departmentRepository.save(department);
                result = "Inserted";
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
