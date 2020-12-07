/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Location;
import com.mcc40.crud.entities.Region;
import com.mcc40.crud.repositories.LocationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author asus
 */
@Service
public class LocationService {
    
    LocationRepository locationRepository;
    
    @Autowired
    public LocationService(LocationRepository locationRepository){
        this.locationRepository = locationRepository;
    }
    
    public List<Location> getAllLocation(){
        return locationRepository.findAll();
    }
    
    public Location getByLocationId(int id) {
        return locationRepository.findById(id).get();
    }
    
    //insert
    public String saveLocation(Location location) {
        String result = null;
        Optional<Location> optionalLocation = locationRepository.findById(location.getLocationId());
        try {
            if (optionalLocation.isPresent() == false) {
                locationRepository.save(location);
                result = "Inserted";
            } else if (optionalLocation.get().equals(true)) {
                Location oldLocation = optionalLocation.get();
                oldLocation.setLocationId(location.getLocationId());
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        return result;
    }

    //delete
    public boolean deleteLocation(int id) {
        locationRepository.deleteById(id);
        return !locationRepository.findById(id).isPresent();
    }
}
