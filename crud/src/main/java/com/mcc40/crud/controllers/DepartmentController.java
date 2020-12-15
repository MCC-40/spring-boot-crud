/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers;

import com.mcc40.crud.entities.Department;
import com.mcc40.crud.services.DepartmentService;
import com.mcc40.crud.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Mochamad Yusuf
 */
@Controller
@RequestMapping("department")
public class DepartmentController {

    DepartmentService service;
    NotificationService notificationService;

    @Autowired
    public DepartmentController(DepartmentService service, NotificationService notificationService) {
        this.service = service;
        this.notificationService = notificationService;
    }

    

    @RequestMapping("") //localhost:8081/
    public String departmentGetAll() {
        for (Department department : service.getAllDepartments()) {
            System.out.println(department.getId()+ " | " + department.getName());
        }
        return "index"; //index.html
    }

    @RequestMapping("find")
    public String getDepartmentById(int id) {
        System.out.println(service.getByIdDepartment(id).getId()+ " | "
                + service.getByIdDepartment(id).getName());
        return "index"; //index.html
    }

    @RequestMapping("save")
    public String departmentSave(int id, String name) {
        Department department = new Department();
        department.setId(id);
        department.setName(name);
        System.out.println(service.saveDepartment(department));
        return "index"; //index.html
    }

    @RequestMapping("delete")
    public String deleteDepartmentById(int id) {
        System.out.println("Mencoba menghapus: " + service.getByIdDepartment(id).getName());
        System.out.println(service.deleteById(id) ? "Delete berhasil" : "Delete gagal");
        return "index"; //index.html
    }

    @RequestMapping("test-yoshua")
    public String getFirstNameLocation() {
        service.getDepartmentNameAndJobTitle();
        return "index";
    }
    
//    @RequestMapping("email")
//    public String sendEmailNotification(){
//        notificationService.javaSimpleEmail("yusufmochamad.sch.acc@gmail.com", "This is email", "This is body");
//        return "index";
//    }

}
