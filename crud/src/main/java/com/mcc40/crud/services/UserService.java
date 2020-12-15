/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.controllers.restfuls.UserRestController;
import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Role;
import com.mcc40.crud.entities.Status;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.EmployeeRepository;
import com.mcc40.crud.repositories.UserRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mochamad Yusuf
 */
@Service
public class UserService {

    UserRepository userRepository;
    EmployeeRepository employeeRepository;
    NotificationService notificationService;

    @Autowired
    public UserService(UserRepository userRepository, EmployeeRepository employeeRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
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
        Optional<User> users = userRepository.findByEmail(username);
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

    public Map<String, Object> login(String username, String password) {
        Map map = new LinkedHashMap();
        Map obj = new LinkedHashMap();
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);

        if (optionalUser.isPresent()) {                                 // User exist
            User user = optionalUser.get();
            Integer userStatus = user.getStatus().getId();
            if (user.getPassword().equals(password)) {   // Comparing password
                switch (userStatus) {
                    case -1:
                        map.put("status", "unverified email log in");
                        break;
                    case 0:
                    case 1:
                    case 2:
                        userStatus = 0;
                        user.setStatus(new Status(0));
                        userRepository.save(user);

//                        loggedUser = user;
                        obj.put("id", user.getId());
                        obj.put("email", user.getEmployee().getEmail());

                        List<String> roles = new ArrayList<>();
                        for (Role role : user.getRoleList()) {
                            roles.add(role.getName());
                        }
                        obj.put("role", roles);
                        map.put("status", userStatus);
                        map.put("user", obj);
                        map.put("entity", user);
                        break;
                    case 3:
                        map.put("status", userStatus);
                        map.put("description", "used banned");
                    default:
                        map.put("description", "unknown error");
                }
            } else {
                switch (userStatus) {
                    case 0:
                    case 1:
                    case 2:
                        userStatus++;
                        user.setStatus(new Status(userStatus));
                        userRepository.save(user);

                        map.put("status", userStatus);
                        map.put("description", "wrong password");
                        break;
                    default:
                        map.put("status", userStatus);
                        map.put("description", "used banned");
                        break;
                }
            }
            return map;

        } else {
            map.put("status", -1);
            map.put("description", "no username or email registered");
            return map;
        }
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

    public boolean requestPasswordReset(String email) {
        User user = getUserByEmail(email);
        if (user != null) {
            try {
                user.setVerificationCode(UUID.randomUUID().toString());     // Re generating verification code
                saveUser(user);

                notificationService.sendEmail(user,
                        "Reset password",
                        "<html><body>"
                        + "Reset your password "
                        + "<a href='http://Localhost:8082/users/forgot-password/" + user.getVerificationCode()
                        + "'>here</a>"
                        + "</body></html>");
            } catch (MessagingException ex) {
                Logger.getLogger(UserRestController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean resetPassword(String verificationCode, String password) {
        Optional<User> optionalUser = userRepository.findByVerificationCode(verificationCode);
        if (optionalUser.isPresent() && optionalUser.get().getStatus().getId() != -1) {
            User user = optionalUser.get();
            user.setPassword(password);
            user.setStatus(new Status(0));
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

}
