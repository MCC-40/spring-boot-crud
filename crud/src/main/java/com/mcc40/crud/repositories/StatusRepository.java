/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.repositories;

import com.mcc40.crud.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author asus
 */
public interface StatusRepository extends JpaRepository<Status, Integer>{
    
}
