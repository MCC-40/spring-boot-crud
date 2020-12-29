/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.Job;
import com.mcc40.crud.services.JobService;
import com.mcc40.crud.services.NotificationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Yoshua
 */
@RestController
@RequestMapping("api/job")
public class JobRestController {

    JobService service;
    NotificationService notificationService;

    @Autowired
    public JobRestController(JobService service, NotificationService notificationService) {
        this.service = service;
        this.notificationService = notificationService;
    }

    @GetMapping("")
    public ResponseEntity<Job> getById(String id) {
        return ResponseEntity.ok().body(service.getByIdJob(id));
    }

    @GetMapping("search")
    public ResponseEntity<List<Job>> searchJob(@RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword == null) {
            keyword = "";
        }
        List<Job> result = service.getAllJob(keyword);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> insertJob(@Validated @RequestBody Job job) {
        Map status = new HashMap();
        if (service.isJobPresent(job.getId())) {
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
        if (!service.isJobPresent(job.getId())) {
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
