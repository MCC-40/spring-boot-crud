/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Country;
import com.mcc40.crud.repositories.CountryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mochamad Yusuf
 */
@Service
public class CountryService {

    @Autowired
    CountryRepository countryRepository;
    
    public List<Country> getAllCountries(){
        return countryRepository.findAll();
    }
    
    public Country getByIdCountry(String id){
        return countryRepository.findById(id).get();
    }
    
    public String saveCountry(Country country){
        String result = null;
        Optional<Country> optionalCountry = countryRepository.findById(country.getId());
        try {
            if (optionalCountry.isPresent() == false) {
                countryRepository.save(country);
                result = "Inserted";
            } else {
                Country oldCountry = optionalCountry.get();
                oldCountry.setName(country.getName());
                countryRepository.save(oldCountry);
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        return result;
    }
    
    public boolean deleteById(String id){
        countryRepository.deleteById(id);
        return !countryRepository.findById(id).isPresent();
    }
}
