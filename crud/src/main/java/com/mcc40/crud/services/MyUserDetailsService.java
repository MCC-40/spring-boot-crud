/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.MyUserDetails;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mochamad Yusuf
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public MyUserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail);
        System.out.println("searching: " + usernameOrEmail);
        if (user.isPresent()) {
            System.out.println("present");
            MyUserDetails userDetails = new MyUserDetails(user.get());
            return userDetails;
        }else{
            System.out.println("null");
            return null;
        }
    }
}
