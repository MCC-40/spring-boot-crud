/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers;

import com.mcc40.crud.entities.Region;
import com.mcc40.crud.services.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author aqira
 */
@Controller
public class RegionController {

    RegionService service;

    @Autowired
    public RegionController(RegionService service) {
        this.service = service;
    }

    
    
    @RequestMapping("") //localhost:8081/
    public String index() {
        for (Region region : service.getAllRegion()) {
            System.out.println(region.getRegionId() + " | " + region.getRegionName());
        }
        return "index"; //index.html
    }
    
    @RequestMapping("getjobs") //localhost:8081/
    public String getJobs(int id) {
        service.getJobs(id);
        return "index"; //index.html
    }
}
