/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Status;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.EmployeeRepository;
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
    EmployeeRepository employeeRepository;

    @Autowired
    public UserService(UserRepository userRepository, EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
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

    //get by id
//    public User getUserByVerificationCode(String verificationCode) {
//        Optional<User> user = userRepository.findByVerificationCode(verificationCode);
//        System.out.println(user.isPresent());
//        if (!user.isPresent()) {
//            return null;
//        } else {
//            return user.get();
//        }
//    }
    public User getUserByUsername(String username) {
        Optional<User> users = userRepository.findByUserName(username);
        if (!users.isPresent()) {
            return null;
        } else {
            return users.get();
        }
    }

    public String saveUser(User user) {
        Optional<Employee> emp = employeeRepository.findById(user.getId());
        if (emp.isPresent()) {
            user.setEmployee(emp.get());
        }
        userRepository.save(user);
        return "success";
    }
//
//    //insert
//    public String insertUser(User user) {
//        String result = "Unknown Error";
//        Optional<User> optionalUser = userRepository.findById(user.getId());
//        try {
//            if (optionalUser.isPresent() == false) {
//                userRepository.save(user);
//                result = "Inserted";
//            } else {
////                User oldUser = optionalUser.get();
////                oldUser.setName(user.getName());
////                user = oldUser;
////                result = "Updated";
//                result = "Id already exist";
//            }
//        } catch (Exception e) {
//            result = "Unknown Error";
//            System.out.println(e.toString());
//        }
//        userRepository.save(user);
//        return result;
//    }
//
//    public String putUser(User user) {
//        String result = "Unknown Error";
//        Optional<User> optionalUser = userRepository.findById(user.getId());
//        try {
//            if (optionalUser.isPresent() == false) {
////                userRepository.save(user);
////                result = "Inserted";
//                result = "Id is not exist";
//            } else {
//                User oldUser = optionalUser.get();
//                if (user.getUserName() != null) {
//                    oldUser.setUserName(user.getUserName());
//                }
//                if (user.getPassword() != null) {
//                    oldUser.setPassword(user.getPassword());
//                }
//                user = oldUser;
//                result = "Updated";
//            }
//        } catch (Exception e) {
//            result = "Unknown Error";
//            System.out.println(e.toString());
//        }
//        userRepository.save(user);
//        return result;
//    }

    //delete
    public boolean deleteUser(int id) {
        userRepository.deleteById(id);
        return !userRepository.findById(id).isPresent();
    }

    public boolean verify(String verificationCode) {
        Optional<User> user = userRepository.findByVerificationCode(verificationCode);
        if (user.isPresent()) {
            user.get().setStatus(new Status(0));
            userRepository.save(user.get());
            return true;
        } else {
            return false;
        }
    }
}
