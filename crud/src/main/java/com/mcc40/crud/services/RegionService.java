/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Country;
import com.mcc40.crud.entities.Department;
import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Location;
import com.mcc40.crud.entities.Region;
import com.mcc40.crud.repositories.RegionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    public Region getById(int id) {
        Optional<Region> region = regionRepository.findById(id);
        if (region.isPresent()) {
            return region.get();
        } else {
            return null;
        }
    }

    public List<Region> getByKeyword(String keyword) {
        List<Region> regionList = new ArrayList<>();

        regionList = regionRepository.findAll();
        if (keyword != null) {
            regionList = regionList.stream().filter(d
                    -> d.getId().toString().contains(keyword)
                    || d.getName().toString().contains(keyword)
            ).collect(Collectors.toList());
        }

        return regionList;
    }
    
    //insert
    public String insert(Region region) {
        String result = null;
        Optional<Region> optionalRegion = regionRepository.findById(region.getId());
        try {
            if (!optionalRegion.isPresent()) {
                regionRepository.save(region);
                result = "Inserted";
            } else {
                result = "Region already exist";
            }
        } catch (Exception e) {
            result = "Region insert error";
            System.out.println(e.toString());
        }
        return result;
    }

    public String update(Region region) {
        String result = "Unknown Error";
        Optional<Region> optionalRegion = regionRepository.findById(region.getId());
        try {
            if (!optionalRegion.isPresent()) {
                result = "Region not exist";
            } else {
                Region oldRegion = optionalRegion.get();
                oldRegion.setName(region.getName());
                region = oldRegion;
                regionRepository.save(region);
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Region update error";
            System.out.println(e.toString());
        }
        return result;
    }

    //delete
    public boolean deleteById(int id) {
        regionRepository.deleteById(id);
        return !regionRepository.findById(id).isPresent();
    }

    public void getJobs(int id) {

        Region region = regionRepository.findById(id).get();
        List<Country> countryList = region.getCountryList();
        for (Country country : countryList) {

            List<Location> locationList = country.getLocationList();
            for (Location location : locationList) {

                List<Department> departmentList = location.getDepartmentList();
                for (Department department : departmentList) {

                    List<Employee> employeeList = department.getEmployeeList();
                    for (Employee employee : employeeList) {

                        System.out.print(employee.getJob().getTitle() + " | ");
                        System.out.print(employee.getFirstName() + " | ");
//                        System.out.print(department.getDepartmentName()+ " | ");
//                        System.out.print(location.getLocationId() + " | ");
//                        System.out.print(country.getCountryName()+ " | ");
                        System.out.println(region.getName());
                    }
                }
            }
        }
    }

}
