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
 * @author asus
 */
@RestController
@RequestMapping("api/locations")
public class LocationRestController {
    
    LocationService service;
    
    @Autowired
    public LocationRestController(LocationService service){
        this.service = service;
    }
    
    //Get All
    @GetMapping("")
    public ResponseEntity<List<Location>> getAllLocation() {
        return ResponseEntity.status(200).body(service.getAllLocation());
    }

    //Get by keyword
    @GetMapping("search")
    public ResponseEntity <List<Location>> getLocation(String keyword){
        List<Location> locations = service.getAllLocation();
        List<Location> result = (List<Location>) locations
                .stream()
                .filter((location) ->
                        location.getId().toString().contains(keyword) ||
//                        location.getStreetAddress().contains(keyword) ||
//                        location.getPostalCode().contains(keyword) ||
                        location.getCity().contains(keyword)
//                        location.getStateProvince().contains(keyword)
                    ).collect(Collectors.toList());
        return ResponseEntity.status(200).body(result);
    }
    
    @PostMapping("")
    public ResponseEntity<Map<String, String>> saveLocation(@RequestBody Location location){
        Map status = new HashMap();
        System.out.println(location);
        if (location.getId() == null) {
            status.put("Status", "No Content");
            return ResponseEntity.status(200).body(status);
        }
        String result = service.saveLocation(location);
        status.put("Status", result);
        if (result.equals("Inserted") || result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        }
        return ResponseEntity.status(500).body(status);
    }
    
    @PutMapping("")
    public ResponseEntity<Map<String, String>> saveLocationPut(@RequestBody Location location) {
        System.out.println("Incoming put");
        System.out.println(location);
        Map status = new HashMap();

        String result = service.saveLocation(location);
        status.put("Status", result);
        if (result.equals("Inserted") || result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }
    
    //delete by id
    @DeleteMapping("delete")
    public String deleteLocation(Integer id){
        Location location = new Location();
        location = service.getByLocationId(id);
        if(location != null){
            service.deleteLocation(id);    
            return "Delete Success";
        }
        return "Delete Failed";
    }
    
    @RequestMapping("test")
    public String test(){
        service.test();
        return "index";
    }
}
