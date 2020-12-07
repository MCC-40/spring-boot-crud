/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.repositories;

import com.mcc40.crud.entities.Region;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author aqira
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Integer>{
    
    @Query(value = "SELECT * FROM regions",nativeQuery = true)
    public List<Region> getAllSql();
}
