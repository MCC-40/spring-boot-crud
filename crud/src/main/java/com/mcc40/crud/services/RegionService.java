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
        String result = "Unknown Error";
        Optional<Region> optionalRegion = regionRepository.findById(region.getId());
        try {
            if (optionalRegion.isPresent() == false) {
                regionRepository.save(region);
                result = "Inserted";
            } else {
                Region oldRegion = optionalRegion.get();
                oldRegion.setName(region.getName());
                region = oldRegion;
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        regionRepository.save(region);
        return result;
    }

    //delete
    public boolean deleteRegion(int id) {
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
