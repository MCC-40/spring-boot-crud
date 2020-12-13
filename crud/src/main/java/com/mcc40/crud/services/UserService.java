/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mochamad Yusuf
 */
@Service
public class UserService {

    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //get all 
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //get by id
    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        System.out.println(user.isPresent());
        if (!user.isPresent()) {
            return null;
        } else {
            return user.get();
        }
    }

    public User getUserByUsername(String username) {
        List<User> users = userRepository.findByUserName(username);
        if (users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    //insert
    public String insertUser(User user) {
        String result = "Unknown Error";
        Optional<User> optionalUser = userRepository.findById(user.getId());
        try {
            if (optionalUser.isPresent() == false) {
                userRepository.save(user);
                result = "Inserted";
            } else {
//                User oldUser = optionalUser.get();
//                oldUser.setName(user.getName());
//                user = oldUser;
//                result = "Updated";
                result = "Id already exist";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        userRepository.save(user);
        return result;
    }

    public String putUser(User user) {
        String result = "Unknown Error";
        Optional<User> optionalUser = userRepository.findById(user.getId());
        try {
            if (optionalUser.isPresent() == false) {
//                userRepository.save(user);
//                result = "Inserted";
                result = "Id is not exist";
            } else {
                User oldUser = optionalUser.get();
                if (user.getUserName() != null) {
                    oldUser.setUserName(user.getUserName());
                }
                if (user.getPassword() != null) {
                    oldUser.setPassword(user.getPassword());
                }
                user = oldUser;
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        userRepository.save(user);
        return result;
    }

    //delete
    public boolean deleteUser(int id) {
        userRepository.deleteById(id);
        return !userRepository.findById(id).isPresent();
    }

    public void test() {
        System.out.println("Hello gaes");
    }
}
