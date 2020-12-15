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
import java.util.stream.Collectors;
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

    public User getUserByUsername(String username) {
        Optional<User> users = userRepository.findByUserName(username);
        if (!users.isPresent()) {
            return null;
        } else {
            return users.get();
        }
    }
    
    public User getUserByEmail(String username) {
        List<User> userList = userRepository.findAll();
        if (username != null) {
            userList = userList.stream().filter(d
                    -> d.getEmployee().getEmail().toString().equals(username)
            ).collect(Collectors.toList());
        }
        if (userList.size() == 1) {
            return userList.get(0);
        } else {
            return null;
        }
    }

    public User getUserByUsernameOrEmail(String username) {
        List<User> userList = userRepository.findAll();
        if (username != null) {
            userList = userList.stream().filter(d
                    -> d.getUserName().toString().equals(username)
                    || d.getEmployee().getEmail().toString().equals(username)
            ).collect(Collectors.toList());
        }
        if (userList.size() == 1) {
            return userList.get(0);
        } else {
            return null;
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
    
    public boolean resetPassword(String verificationCode, String password){
         Optional<User> user = userRepository.findByVerificationCode(verificationCode);
        if (user.isPresent() && user.get().getStatus().getId() != -1) {
            user.get().setPassword(password);
            user.get().setStatus(new Status(0));
            userRepository.save(user.get());
            return true;
        } else {
            return false;
        }
    }
}
