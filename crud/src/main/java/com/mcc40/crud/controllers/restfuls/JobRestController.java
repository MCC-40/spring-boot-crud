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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mochamad Yusuf
 */
@RestController
@RequestMapping("api/jobs")
public class JobRestController {

    JobService service;

    @Autowired
    public JobRestController(JobService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Job>> getByKeyword(String keyword) {
        System.out.println(keyword);
        List<Job> mapList = service.getByKeyword(keyword);
        if (mapList.size() > 0) {
            return ResponseEntity.status(200).body(mapList);
        } else {
            return ResponseEntity.status(404).build();
        }
    }
    
    @GetMapping("id")
    public ResponseEntity<Job> getById(String id) {
        System.out.println(id);
        Job map = service.getById(id);
        if (map != null) {
            return ResponseEntity.status(200).body(map);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> insert(@RequestBody Job job) {
        System.out.println(job);
        Map status = new HashMap();
        
        String result = service.insert(job);
        status.put("Status", result);
        if (result.equals("Inserted")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> update(@RequestBody Job job) {
        System.out.println(job);
        Map status = new HashMap();

        String result = service.update(job);
        status.put("Status", result);
        
        if (result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> delete(String id) {
        Map status = new HashMap();
        if (service.deleteById(id)) {
            status.put("Status", "Success");
            return ResponseEntity.accepted().body(status);
        } else {
            status.put("Status", "Failed");
            return ResponseEntity.status(500).body(status);
        }
    }

}
