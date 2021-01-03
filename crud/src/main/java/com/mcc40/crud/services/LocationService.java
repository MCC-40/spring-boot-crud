/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Department;
import com.mcc40.crud.entities.Location;
import com.mcc40.crud.repositories.LocationRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> getAll() {
        return locationRepository.findAll();
    }

    public Location getById(int id) {
        return locationRepository.findById(id).get();
    }

    public List<Location> getByKeyword(String keyword) {
        List<Location> locationList = new ArrayList<>();

        locationList = locationRepository.findAll();
        if (keyword != null) {
            locationList = locationList.stream().filter(d
                    -> d.getId().toString().contains(keyword)
                    || d.getStreetAddress().toString().contains(keyword)
                    || d.getStateProvince().toString().contains(keyword)
                    || d.getPostalCode().toString().contains(keyword)
                    || d.getCity().toString().contains(keyword)
                    || d.getCountry().getName().toString().contains(keyword)
            ).collect(Collectors.toList());
        }

        return locationList;
    }
    //insert

    public String insert(Location location) {
        String result = null;
        Optional<Location> optionalLocation = locationRepository.findById(location.getId());
        try {
            if (optionalLocation.isPresent() == false) {
                locationRepository.save(location);
                result = "Inserted";
            } else if (optionalLocation.get().equals(true)) {
                result = "Location already exist";
            }
        } catch (Exception e) {
            result = "Location insert Error";
            System.out.println(e.toString());
        }
        return result;
    }

    public String update(Location location) {
        String result = null;
        Optional<Location> optionalLocation = locationRepository.findById(location.getId());
        try {
            if (!optionalLocation.isPresent()) {
                result = "Location not exist";
            } else if (optionalLocation.get().equals(true)) {
                Location oldLocation = optionalLocation.get();
                oldLocation.setId(location.getId());
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Location update Error";
            System.out.println(e.toString());
        }
        return result;
    }

    //delete
    public boolean deleteById(int id) {
        locationRepository.deleteById(id);
        return !locationRepository.findById(id).isPresent();
    }

    public void test() {
        Location location = locationRepository.findById(1700).get();
        List<Department> departmentList = location.getDepartmentList();
        for (Department department : departmentList) {
            System.out.print(department.getName() + " | ");
            System.out.println(department.getLocation().getId());
        }
    }
}
