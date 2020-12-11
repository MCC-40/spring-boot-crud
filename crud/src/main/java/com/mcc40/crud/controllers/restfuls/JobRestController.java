/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.Job;
import com.mcc40.crud.services.JobService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Yoshua
 */
@RestController
@RequestMapping("api/job")
public class JobRestController {

    JobService service;

    @Autowired
    public JobRestController(JobService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<Job> getById(String id) {
        return ResponseEntity.ok().body(service.getByIdJob(id));
    }

    @GetMapping("search")
    public ResponseEntity<List<Job>> searchJob(String keyword) {
        List<Job> jobs = service.getAllJob();
        List<Job> result = jobs.stream()
                .filter(job
                        -> job.getId().toLowerCase().contains(keyword.toLowerCase())
                || job.getTitle().toLowerCase().contains(keyword.toLowerCase())
                || Integer.toString(job.getMinSalary()).toLowerCase().contains(keyword)
                || Integer.toString(job.getMaxSalary()).toLowerCase().contains(keyword)
                )
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> insertJob(@Validated @RequestBody Job job) {
        Map status = new HashMap();
        if (job.getId() == null) {
            status.put("Status", "Id not found");
            return ResponseEntity.status(200).body(status);
        } else if (service.isJobPresent(job.getId())) {
            status.put("Status", "Use Method PUT to update");
            return ResponseEntity.status(200).body(status);
        }
        String result = service.saveJob(job);
        status.put("Status", result);
        if (result.equals("Inserted")) {
            return ResponseEntity.accepted().body(status);
        }
        return ResponseEntity.status(500).body(status);
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateJob(@Validated @RequestBody Job job) {
        Map status = new HashMap();
        if (job.getId() == null) {
            status.put("Status", "No Content");
            return ResponseEntity.status(200).body(status);
        } else if (!service.isJobPresent(job.getId())) {
            status.put("Status", "Use Method POST to insert new data");
            return ResponseEntity.status(200).body(status);
        }
        
        String result = service.saveJob(job);
        status.put("Status", result);
        if (result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        }
        return ResponseEntity.status(500).body(status);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteJob(String id) {
        if (service.deleteJob(id)) {
            return ResponseEntity.status(200).body("Delete Success");
        }
        return ResponseEntity.status(500).body("Delete Fail");
    }
}
