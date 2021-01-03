/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers;

import com.mcc40.crud.entities.Location;
import com.mcc40.crud.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author asus
 */
@Controller
@RequestMapping("location")
public class LocationController {
    
    LocationService service;
    
    @Autowired
    public LocationController(LocationService service){
        this.service = service;
    }
    
    @RequestMapping("")
    public String index(){
        for (Location location : service.getAll()) {
            System.out.println(location.getId()+ " | " + location.getStreetAddress());
        }
        return "index";
    }
    
    @RequestMapping("search")
    public String location(int id){
        Location location = service.getById(id);
            System.out.println(location.getId()+ " | " + location.getStreetAddress());
            return "index";
    }
    
    @RequestMapping("save")
    public String saveLocation(Location location){
       
       System.out.println(service.insert(location));
        
       return "index";
    }
    
    @RequestMapping("delete")
    public String saveLocation(int id){
            System.out.println(service.deleteById(id));
        
        return "index";
    }
    
    @RequestMapping("test")
    public String test(){
        service.test();
        return "index";
    }
}
