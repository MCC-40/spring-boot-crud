/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.Country;
import com.mcc40.crud.services.CountryService;
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
@RequestMapping("api/countries")
public class CountryRestController {

    CountryService service;

    @Autowired
    public CountryRestController(CountryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Country>> getByKeyword(String keyword) {
        System.out.println(keyword);
        List<Country> mapList = service.getByKeyword(keyword);
        if (mapList.size() > 0) {
            return ResponseEntity.status(200).body(mapList);
        } else {
            return ResponseEntity.status(404).build();
        }
    }
    
    @GetMapping("id")
    public ResponseEntity<Country> getById(String id) {
        System.out.println(id);
        Country map = service.getById(id);
        if (map != null) {
            return ResponseEntity.status(200).body(map);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> insert(@RequestBody Country country) {
        System.out.println(country);
        Map status = new HashMap();
        
        String result = service.insert(country);
        status.put("Status", result);
        if (result.equals("Inserted")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> update(@RequestBody Country country) {
        System.out.println(country);
        Map status = new HashMap();

        String result = service.update(country);
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
