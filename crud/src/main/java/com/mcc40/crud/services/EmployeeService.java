/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.repositories.EmployeeRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yoshua
 */
@Service
public class EmployeeService {

    EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    //get all 
    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    //get by id
    public Employee getByIdEmployee(int id) {
        return employeeRepository.findById(id).get();
    }

    //insert
    public String saveEmployee(Employee employee) {
        String result = null;
        Optional<Employee> optionalEmployee = employeeRepository.findById(employee.getEmployeeId());
        try {
            if (optionalEmployee.isPresent() == false) {
                employeeRepository.save(employee);
                result = "Inserted";
            } else if (optionalEmployee.get().equals(true)) {
                Employee oldEmployee = optionalEmployee.get();
                oldEmployee.setFirstName(employee.getFirstName());
                oldEmployee.setLastName(employee.getLastName());
                oldEmployee.setEmail(employee.getEmail());
                oldEmployee.setPhoneNumber(employee.getPhoneNumber());
                oldEmployee.setHireDate(employee.getHireDate());
                oldEmployee.setJobId(employee.getJobId());
                oldEmployee.setSalary(employee.getSalary());
                oldEmployee.setCommissionPct(employee.getCommissionPct());
                oldEmployee.setManagerId(employee.getManagerId());
                oldEmployee.setDepartmentId(employee.getDepartmentId());
                employee = oldEmployee;
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        return result;
    }

    //delete
    public boolean deleteEmployee(int id) {
        employeeRepository.deleteById(id);
        return !employeeRepository.findById(id).isPresent();
    }

    public void getFirstNameAndLocation() {
        List<Employee> employees = employeeRepository.findAll();
        for (Employee employee : employees) {
            System.out.print(employee.getFirstName() + "  |   ");
                System.out.println(employee.getDepartmentId().getLocationId().getLocationId());
        }
    }
}
