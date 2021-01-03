/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Department;
import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Job;
import com.mcc40.crud.repositories.JobRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yoshua
 */
@Service
public class JobService {

    JobRepository jobRepository;

    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    //get all 
    public List<Job> getAllJob() {
        return jobRepository.findAll();
    }

    //get by id
   public Job getById(String id) {
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent()) {
            return job.get();
        } else {
            return null;
        }
    }

    public List<Job> getByKeyword(String keyword) {
        List<Job> jobList = new ArrayList<>();

        jobList = jobRepository.findAll();
        if (keyword != null) {
            jobList = jobList.stream().filter(d
                    -> d.getId().toString().contains(keyword)
                    || d.getTitle().toString().contains(keyword)
            ).collect(Collectors.toList());
        }

        return jobList;
    }

    //insert
    public String insert(Job job) {
        String result = null;
        Optional<Job> optionalJob = jobRepository.findById(job.getId());
        try {
            if (!optionalJob.isPresent()) {
                jobRepository.save(job);
                result = "Inserted";
            } else {
                result = "Job already exist";
            } 
        } catch (Exception e) {
            result = "Job insert error";
            System.out.println(e.toString());
        }
        return result;
    }
    
    public String update(Job job) {
        String result = null;
        Optional<Job> optionalJob = jobRepository.findById(job.getId());
        try {
            if (!optionalJob.isPresent()) {
                result = "Job is not exist";
            } else if (optionalJob.isPresent()) {
                jobRepository.save(job);
                result = "Updated";
            } 
        } catch (Exception e) {
            result = "Job update error";
            System.out.println(e.toString());
        }
        return result;
    }

    //delete
    public boolean deleteById(String id) {
        jobRepository.deleteById(id);
        return !jobRepository.findById(id).isPresent();
    }

    public void getJobTitleAndDepartmentName() {
        List<Job> jobs = jobRepository.findAll();
        for (Job job : jobs) {
            List<Employee> employeeList = job.getEmployeeList();
            for (Employee employee : employeeList) {
                System.out.print(job.getTitle()+ "  |   ");
                System.out.println(employee.getDepartment().getName());
            }
        }
    }
}
