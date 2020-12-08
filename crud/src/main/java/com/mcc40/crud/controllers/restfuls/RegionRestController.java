/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.Department;
import com.mcc40.crud.entities.Region;
import com.mcc40.crud.services.RegionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
@RequestMapping("api/regions")
public class RegionRestController {

    RegionService service;

    @Autowired
    public RegionRestController(RegionService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<List<Region>> getRegionById(Integer id) {
        List<Region> regionList = service.getAllRegion();
        if (id != null) {
            regionList = regionList.stream().filter(d -> d.getId() == id).collect(Collectors.toList());
        }
        if (regionList.size() > 0) {
            return ResponseEntity.status(200).body(regionList);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> regionSave(@RequestBody Region region) {
        System.out.println(region);
        Map status = new HashMap();

        String result = service.saveRegion(region);
        status.put("Status", result);
        if (result.equals("Inserted") || result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @PutMapping("")
    public ResponseEntity<Map<String, String>> regionSaveWithPut(@RequestBody Region region) {
        System.out.println(region);
        Map status = new HashMap();

        String result = service.saveRegion(region);
        status.put("Status", result);
        if (result.equals("Inserted") || result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<Map<String, String>> deleteRegionById(int id) {
        Map status = new HashMap();
        if (service.deleteRegion(id)) {
            status.put("Status", "Success");
            return ResponseEntity.accepted().body(status);
        } else {
            status.put("Status", "Failed");
            return ResponseEntity.status(500).body(status);
        }
    }

}
