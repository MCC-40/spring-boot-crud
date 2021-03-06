/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Job;
import com.mcc40.crud.repositories.JobRepository;
import java.util.List;
import java.util.Optional;
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
    public Job getByIdJob(String id) {
        return jobRepository.findById(id).get();
    }

    //insert
    public String saveJob(Job job) {
        String result = null;
        Optional<Job> optionalJob = jobRepository.findById(job.getJobId());
        try {
            if (optionalJob.isPresent() == false) {
                jobRepository.save(job);
                result = "Inserted";
            } else if (optionalJob.get().equals(true)) {
                Job oldJob = optionalJob.get();
                oldJob.setJobTitle(job.getJobTitle());
                oldJob.setMinSalary(job.getMinSalary());
                oldJob.setMaxSalary(job.getMaxSalary());
                job = oldJob;
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        return result;
    }

    //delete
    public boolean deleteJob(String id) {
        jobRepository.deleteById(id);
        return !jobRepository.findById(id).isPresent();
    }
}
