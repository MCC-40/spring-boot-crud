/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.repositories;

import com.mcc40.crud.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Yoshua
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByVerificationCode(String verificationCode);

    @Query(value ="INSERT INTO `users`(`id`, `username`, `password`, `verification_code`, `status`) values(?, ?, ?, ?, ?)", nativeQuery = true)
    public void storeData(int id, String username, String password, String verificationCode, int status);
}
