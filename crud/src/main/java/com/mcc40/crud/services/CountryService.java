/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Country;
import com.mcc40.crud.entities.Department;
import com.mcc40.crud.repositories.CountryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public Country getById(String id) {
        Optional<Country> country = countryRepository.findById(id);
        if (country.isPresent()) {
            return country.get();
        } else {
            return null;
        }
    }
    
    public List<Country> getByKeyword(String keyword) {
        List<Country> countryList = new ArrayList<>();

        countryList = countryRepository.findAll();
        if (keyword != null) {
            countryList = countryList.stream().filter(d
                    -> d.getId().toString().contains(keyword)
                    || d.getName().toString().contains(keyword)
            ).collect(Collectors.toList());
        }

        return countryList;
    }

    public String insert(Country country) {
        String result = null;
        Optional<Country> optionalCountry = countryRepository.findById(country.getId());
        try {
            if (!optionalCountry.isPresent()) {
                countryRepository.save(country);
                result = "Inserted";
            } else {
                result = "Country already exist";
            }
        } catch (Exception e) {
            result = "Country insert error";
            System.out.println(e.toString());
        }
        return result;
    }

    public String update(Country country) {
        String result = null;
        Optional<Country> optionalCountry = countryRepository.findById(country.getId());
        try {
            if (!optionalCountry.isPresent()) {
                result = "Country is not exist";
            } else {
                Country oldCountry = optionalCountry.get();
                oldCountry.setName(country.getName());
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Country update error";
            System.out.println(e.toString());
        }
        return result;
    }

    public boolean deleteById(String id) {
        countryRepository.deleteById(id);
        return !countryRepository.findById(id).isPresent();
    }
}
