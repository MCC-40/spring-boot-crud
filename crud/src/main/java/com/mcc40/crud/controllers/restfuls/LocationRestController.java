/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.Location;
import com.mcc40.crud.services.LocationService;
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
@RequestMapping("api/locations")
public class LocationRestController {

    LocationService service;

    @Autowired
    public LocationRestController(LocationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Location>> getByKeyword(String keyword) {
        System.out.println(keyword);
        List<Location> mapList = service.getByKeyword(keyword);
        if (mapList.size() > 0) {
            return ResponseEntity.status(200).body(mapList);
        } else {
            return ResponseEntity.status(404).build();
        }
    }
    
    @GetMapping("id")
    public ResponseEntity<Location> getById(Integer id) {
        System.out.println(id);
        Location map = service.getById(id);
        if (map != null) {
            return ResponseEntity.status(200).body(map);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> insert(@RequestBody Location location) {
        System.out.println("insert = " + location);
        Map status = new HashMap();
        
        String result = service.insert(location);
        status.put("Status", result);
        if (result.equals("Inserted")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> update(@RequestBody Location location) {
        System.out.println("update = " + location);
        Map status = new HashMap();

        String result = service.update(location);
        status.put("Status", result);
        
        if (result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> delete(int id) {
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
