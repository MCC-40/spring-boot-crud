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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    //find all by id
    public List<Employee> searchEmployee() {
        return employeeRepository.findAll();
    }

    //get all 
    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    //get by id
    public Employee getByIdEmployee(int id) {
        return employeeRepository.findById(id).get();
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
