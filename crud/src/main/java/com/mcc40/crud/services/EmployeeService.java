/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Country;
import com.mcc40.crud.entities.Department;
import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Job;
import com.mcc40.crud.entities.Location;
import com.mcc40.crud.repositories.DepartmentRepository;
import com.mcc40.crud.repositories.EmployeeRepository;
import com.mcc40.crud.repositories.JobRepository;
import com.mcc40.crud.repositories.RegionRepository;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    RegionRepository regionRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, JobRepository jobRepository, DepartmentRepository departmentRepository, RegionRepository regionRepository) {
        this.employeeRepository = employeeRepository;
        this.jobRepository = jobRepository;
        this.departmentRepository = departmentRepository;
        this.regionRepository = regionRepository;
    }

    public boolean isEmplyeePresent(int id) {
        return employeeRepository.findById(id).isPresent();
    }

    public static Map<String, Object> MapTheEmployee(Employee employee) {
        Map<String, Object> e = new HashMap<>();
        e.put("id", employee.getId());
        e.put("firstName", employee.getFirstName());
        e.put("lastName", employee.getLastName());
        e.put("email", employee.getEmail());
        e.put("phoneNumber", employee.getPhoneNumber());
        e.put("hireDate", employee.getHireDate());
        e.put("job", employee.getJob().getTitle());
        e.put("salary", employee.getSalary());
        e.put("commissionPct", employee.getCommissionPct());
        e.put("manager", employee.getManager() == null ? null : employee.getManager().getLastName());
        e.put("department", employee.getDepartment() == null ? null : employee.getDepartment().getName());
        return e;
    }

    //find all by id
    public List<Employee> searchEmployee() {
        return employeeRepository.findAll();
    }

    //get all 
    public List<Map<String, Object>> getAllEmployee(String keyword) {
        List<Employee> employees = employeeRepository.findAll();
        List<Employee> result = employees.stream()
                .filter(employee
                        -> Integer.toString(employee.getId()).contains(keyword)
                || employee.getLastName().toLowerCase().contains(keyword.toLowerCase())
                || employee.getFirstName().toLowerCase().contains(keyword.toLowerCase())
                )
                .collect(Collectors.toList());
        List<Map<String, Object>> mapEmployeeList = new ArrayList<>();
        for (Employee employee : result) {
            mapEmployeeList.add(MapTheEmployee(employee));
        }
        return mapEmployeeList;
    }

    public List<Map<String, Object>> getAllManager() {
        List<Employee> managers = employeeRepository.getManagers();
        List<Employee> result = managers.stream().collect(Collectors.toList());
        List<Map<String, Object>> mapEmployeeList = new ArrayList<>();
        for (Employee employee : result) {
            mapEmployeeList.add(MapTheEmployee(employee));
        }
        return mapEmployeeList;
    }

    //get by id
    public Map<String, Object> getByIdEmployee(int id) {
        return MapTheEmployee(employeeRepository.findById(id).get());
    }

    //register
    public String registerEmployee(Map<String, Object> data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String result = "Inserted";
        Employee employee = new Employee();
        employee.setId((Integer) data.get("id"));
        employee.setFirstName(data.get("firstName").toString());
        employee.setLastName(data.get("lastName").toString());
        employee.setPhoneNumber(data.get("phoneNumber").toString());
        employee.setEmail(data.get("email").toString());
        employee.setHireDate(date);
        employee.setCommissionPct(BigDecimal.valueOf((Integer) data.get("commissionPct")));
        employee.setSalary(BigDecimal.valueOf((Integer) data.get("salary")));

        Job job = new Job();
        job.setId(data.get("job").toString());
        employee.setJob(job);

        Employee manager = new Employee();
        manager.setId((Integer) data.get("manager"));
        employee.setManager(manager);

        Department department = new Department();
        department.setId((Integer) data.get("department"));
        employee.setDepartment(department);

        employeeRepository.save(employee);
        return result;
    }

    //insert
    public String saveEmployee(Employee employee) {
        String result = "Inserted";
        Optional<Employee> optionalEmployee = employeeRepository.findById(employee.getId());
        System.out.println(optionalEmployee.isPresent());
        try {
            if (optionalEmployee.isPresent() == true) {
                Employee oldEmployee = optionalEmployee.get();
                oldEmployee.setFirstName(employee.getFirstName());
                oldEmployee.setLastName(employee.getLastName());
                oldEmployee.setEmail(employee.getEmail());
                oldEmployee.setPhoneNumber(employee.getPhoneNumber());
                oldEmployee.setHireDate(employee.getHireDate());
                oldEmployee.setJob(employee.getJob());
                oldEmployee.setSalary(employee.getSalary());
                oldEmployee.setCommissionPct(employee.getCommissionPct());
                oldEmployee.setManager(employee.getManager());
                oldEmployee.setDepartment(employee.getDepartment());
                employee = oldEmployee;
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        employeeRepository.save(employee);
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
