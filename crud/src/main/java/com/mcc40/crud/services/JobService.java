/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Job;
import com.mcc40.crud.repositories.JobRepository;
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

    public boolean isJobPresent(String id) {
        return jobRepository.findById(id).isPresent();
    }

    //get all 
    public List<Job> getAllJob(String keyword) {
        List<Job> jobs = jobRepository.findAll();
        List<Job> result = jobs.stream()
                .filter(job
                        -> job.getId().toLowerCase().contains(keyword.toLowerCase())
                || job.getTitle().toLowerCase().contains(keyword.toLowerCase())
                || Integer.toString(job.getMinSalary()).toLowerCase().contains(keyword)
                || Integer.toString(job.getMaxSalary()).toLowerCase().contains(keyword)
                )
                .collect(Collectors.toList());
        return result;
    }

    //get by id
    public Job getByIdJob(String id) {
        return jobRepository.findById(id).get();
    }

    //insert
    public String saveJob(Job job) {
        String result = "Inserted";
        Optional<Job> optionalJob = jobRepository.findById(job.getId());
        try {
            if (optionalJob.isPresent() == true) {
                Job oldJob = optionalJob.get();
                oldJob.setTitle(job.getTitle());
                oldJob.setMinSalary(job.getMinSalary());
                oldJob.setMaxSalary(job.getMaxSalary());
                job = oldJob;
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        jobRepository.save(job);
        return result;
    }

    //delete
    public boolean deleteJob(String id) {
        jobRepository.deleteById(id);
        return !jobRepository.findById(id).isPresent();
    }

    public void getJobTitleAndDepartmentName() {
        List<Job> jobs = jobRepository.findAll();
        for (Job job : jobs) {
            List<Employee> employeeList = job.getEmployeeList();
            for (Employee employee : employeeList) {
                System.out.print(job.getTitle() + "  |   ");
                System.out.println(employee.getDepartment().getName());
            }
        }
    }
}
