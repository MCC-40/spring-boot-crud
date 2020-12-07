/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers;

import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Yoshua
 */
@Controller
@RequestMapping("employee")
public class EmployeeController {

    EmployeeService service;

    @Autowired
    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @RequestMapping("") 
    public String getAllEmployee() {
        for (Employee employee : service.getAllEmployee()) {
            System.out.println(employee.getFirstName() + " | " + employee.getLastName());
        }
        return "index"; //index.html
    }

    @RequestMapping("search") 
    public String getByIdEmployee(int id) {
        Employee employee = service.getByIdEmployee(id);
        System.out.println(employee.getFirstName() + " | " + employee.getLastName());
        return "index";
    }

    @RequestMapping("save") 
    public String saveEmployee(Employee employee) {
        System.out.println(service.saveEmployee(employee));
        return "index"; 
    }
//
    @RequestMapping("delete") 
    public String deleteEmployee(int id) {
        if (service.deleteEmployee(id)) {
            System.out.println("Delete Success");
        } else {
            System.out.println("Delete Fail");
        }
        return "index";
    }
    
    @RequestMapping("test")
    public String getFirstNameLocation(){
        service.getFirstNameAndLocation();
        return "index";
    }

}
