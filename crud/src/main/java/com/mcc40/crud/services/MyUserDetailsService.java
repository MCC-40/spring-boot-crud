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

    UserRepository userRepository;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String string) throws UsernameNotFoundException {
        Optional<User> findByUsernameOrEmail = userRepository.findByUsernameOrEmail(string);

        if (findByUsernameOrEmail.isPresent()) {
            return new MyUserDetails(findByUsernameOrEmail.get());
        } else {
            return null;
        }
    }

}
