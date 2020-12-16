/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers;

import com.mcc40.crud.entities.Country;
import com.mcc40.crud.entities.Region;
import com.mcc40.crud.services.CountryService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Mochamad Yusuf
 */
@Controller
@RequestMapping("country")
public class CountryController {

    CountryService service;

    @Autowired
    public CountryController(CountryService service) {
        this.service = service;
    }

    @RequestMapping("") //localhost:8081/
    public String countryGetAll() {
        for (Country country : service.getAllCountries()) {
            System.out.println(country.getId()+ " | " + country.getName());
        }
        return "index"; //index.html
    }

    @RequestMapping("find")
    public String getCountryById(String id) {
        System.out.println(service.getByIdCountry(id).getId()+ " | "
                + service.getByIdCountry(id).getName());
        return "index"; //index.html
    }

    @PostMapping("save")
    public String countrySave(@RequestBody Map<String, String> input) {
        String id = input.get("id");
        String name = input.get("name");
        Integer regionId = Integer.parseInt(input.get("regionId"));
        Country country = new Country();
        Region region = new Region();
        region.setId(regionId);
        country.setId(id);
        country.setName(name);
        country.setRegion(region);
        System.out.println(service.saveCountry(country));
        return "index"; //index.html
    }
    
    @RequestMapping("delete")
    public String deleteCountryById(String id) {
        System.out.println("Mencoba menghapus: " + service.getByIdCountry(id).getName());
        System.out.println(service.deleteById(id) ? "Delete berhasil" : "Delete gagal");
        return "index"; //index.html
    }
}
