/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers;

import com.mcc40.crud.entities.Job;
import com.mcc40.crud.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Yoshua
 */
@Controller
@RequestMapping("job")
public class JobController {

    JobService service;

    @Autowired
    public JobController(JobService service) {
        this.service = service;
    }

    @RequestMapping("")
    public String getAllEmployee() {
        for (Job job : service.getAllJob()) {
            System.out.println(job.getTitle()+ " | " + job.getMinSalary() + " | " + job.getMaxSalary());
        }
        return "index"; //index.html
    }

    @RequestMapping("search")
    public String getByIdEmployee(String id) {
        Job job = service.getByIdJob(id);
        System.out.println(job.getTitle()+ " | " + job.getMinSalary() + " | " + job.getMaxSalary());
        return "index";
    }

    @RequestMapping("save")
    public String saveEmployee(Job job) {
        System.out.println(service.insert(job));
        return "index";
    }
//

    @RequestMapping("delete")
    public String deleteJob(String id) {
        if (service.deleteById(id)) {
            System.out.println("Delete Success");
        } else {
            System.out.println("Delete Fail");
        }
        return "index";
    }

    @RequestMapping("test-yoshua")
    public String getFirstNameLocation() {
        service.getJobTitleAndDepartmentName();
        return "index";
    }

}
