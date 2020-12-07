/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Region;
import com.mcc40.crud.repositories.RegionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author aqira
 */
@Service
public class RegionService {

    RegionRepository regionRepository;

    @Autowired
    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    //get all 
    public List<Region> getAllRegion() {
        return regionRepository.getAllSql();
    }

    //get by id
    public Region getByIdRegion(int id) {
        return regionRepository.findById(id).get();
    }

    //insert
    public String saveRegion(Region region) {
        String result = null;
        Optional<Region> optionalRegion = regionRepository.findById(region.getRegionId());
        try {
            if (optionalRegion.isPresent() == false) {
                regionRepository.save(region);
                result = "Inserted";
            } else if (optionalRegion.get().equals(true)) {
                Region oldRegion = optionalRegion.get();
                oldRegion.setRegionName(region.getRegionName());
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        return result;
    }

    //delete
    public boolean deleteRegion(int id) {
        regionRepository.deleteById(id);
        return !regionRepository.findById(id).isPresent();
    }

}
