/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.repositories;

import com.mcc40.crud.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mochamad Yusuf
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    public Optional<User> findByUserName(String userName);
    
    public Optional<User> findByVerificationCode(String verificationCode);
    
}
