/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Country;
import com.mcc40.crud.entities.Department;
import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Location;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.DepartmentRepository;
import com.mcc40.crud.repositories.EmployeeRepository;
import com.mcc40.crud.repositories.JobRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yoshua
 */
@Service
public class EmployeeService {

    EmployeeRepository employeeRepository;
    JobRepository jobRepository;
    DepartmentRepository departmentRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, JobRepository jobRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.jobRepository = jobRepository;
        this.departmentRepository = departmentRepository;
    }

    public boolean isJobPresent(int id) {
        Optional<Employee> optionalJob = employeeRepository.findById(id);
        return optionalJob.isPresent();
    }

    public List<Employee> getByKeyword(String keyword) {
        List<Employee> employeeList = new ArrayList<>();
        List<Map<String, Object>> mapList = new ArrayList<>();

        if (keyword != null) {
            employeeList = employeeRepository.findAll();
            employeeList = employeeList.stream().filter(d
                    -> d.getId().toString().contains(keyword)
                    || d.getFirstName().toString().contains(keyword)
                    || d.getLastName().toString().contains(keyword)
                    || d.getEmail().toString().contains(keyword)
                    || d.getPhoneNumber().toString().contains(keyword)
            ).collect(Collectors.toList());
        } else {
            employeeList = employeeRepository.findAll();
        }

        for (Employee employee : employeeList) {
            mapList.add(employee.getJsonProperties());
        }
        return employeeList;
    }
    
    //get all 
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    //get by id
    public Employee getById(int id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        System.out.println(employee.isPresent());
        if (!employee.isPresent()) {
            return null;
        } else {
            return employee.get();
        }
    }

    //insert
    public String insert(Employee employee) {
        String result = null;
        Optional<Employee> optionalEmployee = employeeRepository.findById(employee.getId());
        if (optionalEmployee.isPresent() == false) {
            employee.setJob(jobRepository.findById(employee.getJob().getId()).get());
            employee.setManager(employeeRepository.findById(employee.getManager().getId()).get());
            employee.setDepartment(departmentRepository.findById(employee.getDepartment().getId()).get());
            employeeRepository.save(employee);
            result = "Inserted";
        } else {
            Employee oldEmployee = optionalEmployee.get();
            oldEmployee.setFirstName(employee.getFirstName());
            oldEmployee.setLastName(employee.getLastName());
            oldEmployee.setEmail(employee.getEmail());
            oldEmployee.setPhoneNumber(employee.getPhoneNumber());
            oldEmployee.setHireDate(employee.getHireDate());
            employee.setJob(jobRepository.findById(employee.getJob().getId()).get());
            oldEmployee.setSalary(employee.getSalary());
            oldEmployee.setCommissionPct(employee.getCommissionPct());
            oldEmployee.setManager(employeeRepository.findById(employee.getManager().getId()).get());
            employee.setDepartment(departmentRepository.findById(employee.getManager().getId()).get());
            employeeRepository.save(oldEmployee);
            result = "Updated";
        }
        return result;
    }

    //insert
    public String update(Employee employee) {
        String result = null;
        Optional<Employee> optionalEmployee = employeeRepository.findById(employee.getId());
        if (optionalEmployee.isPresent()) {
            Employee oldEmployee = optionalEmployee.get();
            oldEmployee.setFirstName(employee.getFirstName());
            oldEmployee.setLastName(employee.getLastName());
            oldEmployee.setEmail(employee.getEmail());
            oldEmployee.setPhoneNumber(employee.getPhoneNumber());
            oldEmployee.setHireDate(employee.getHireDate());
            employee.setJob(jobRepository.findById(employee.getJob().getId()).get());
            oldEmployee.setSalary(employee.getSalary());
            oldEmployee.setCommissionPct(employee.getCommissionPct());
            oldEmployee.setManager(employeeRepository.findById(employee.getManager().getId()).get());
            employee.setDepartment(departmentRepository.findById(employee.getManager().getId()).get());
            employeeRepository.save(oldEmployee);
            result = "Updated";
        } else {
            result = "Employee not exist";
        }
        return result;
    }

    //delete
    public boolean deleteById(int id) {
        employeeRepository.deleteById(id);
        return !employeeRepository.findById(id).isPresent();
    }

    public void getFirstNameAndLocation() {
        List<Employee> employees = employeeRepository.findAll();
        for (Employee employee : employees) {
            System.out.print(employee.getFirstName() + "  |   ");
            System.out.println(employee.getDepartment().getLocation().getId());
        }
    }

    public void getEmployeeAndCountry() {
        Employee employee = employeeRepository.findById(100).get();
        Department department = employee.getDepartment();

        Location location = department.getLocation();

        Country country = location.getCountry();

        System.out.println(employee.getFirstName() + " | " + country.getName());
    }
}
