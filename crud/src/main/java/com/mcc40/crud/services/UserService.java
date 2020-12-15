/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Role;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.entities.UserStatus;
import com.mcc40.crud.repositories.EmployeeRepository;
import com.mcc40.crud.repositories.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yoshua
 */
@Service
public class UserService {

    private static UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public UserService(UserRepository userRepository, EmployeeRepository employeeRepository) {
        UserService.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    public User getUserById(int id) {
        return userRepository.findById(id).get();
    }

    private static String updateUser(User olduUser, int statusId) {
        User user = new User();
        user.setId(olduUser.getId());
        user.setUsername(olduUser.getUsername());
        user.setPassword(olduUser.getPassword());
        user.setVerificationCode(null);

        System.out.println("ROLES");
        Set<Role> roles = new HashSet<>();
        olduUser.getRoles().forEach((role) -> {
            Role r = new Role();
            r.setId(role.getId());
            roles.add(r);
        });
        user.setRoles(roles);

        UserStatus status = new UserStatus();
        status.setId(statusId);
        user.setStatus(status);

        userRepository.save(user);
        return "Success";
    }

    public Map<String, Object> login(String usernameOrEmail, String password) {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "Not Found");
        Optional<User> optionalUser = userRepository.findByUsername(usernameOrEmail);
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(usernameOrEmail);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            result.clear();
            if (user.getStatus().getId() == -1) {
                result.put("result", "User not Verified");
            } else if (user.getStatus().getId() == 3) {
                result.put("result", "User Banned");
            } else if (user.getPassword().equals(password)) {
                result.put("id", user.getId());
                List<String> listRole = new ArrayList<>();
                user.getRoles().forEach((role) -> {
                    listRole.add(role.getName());
                });
                result.put("roles", listRole);
                result.put("email", user.getEmployee().getEmail());
                updateUser(user, 0);
            } else {
                result.put("result", "Wrong Password");
                updateUser(user, user.getStatus().getId() + 1);
            }
        } else if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            User user = employee.getUser();
            result.clear();
            if (user.getStatus().getId() == -1) {
                result.put("result", "User not Verified");
            } else if (user.getStatus().getId() == 3) {
                result.put("result", "User Banned");
            } else if (user.getPassword().equals(password) && employee.getEmail().equals(usernameOrEmail)) {
                result.clear();
                result.put("id", employee.getUser().getId());
                List<String> listRole = new ArrayList<>();
                user.getRoles().forEach((role) -> {
                    listRole.add(role.getName());
                });
                result.put("roles", listRole);
                result.put("email", employee.getEmail());
                updateUser(user, 0);
            } else {
                result.put("result", "Wrong Password");
                updateUser(user, user.getStatus().getId() + 1);
            }
        }
        return result;
    }

    public String register(User user) {
        Role role = new Role();
        role.setId(3);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        UserStatus status = new UserStatus();
        status.setId(-1);
        user.setStatus(status);

        user.setVerificationCode(UUID.randomUUID().toString());
        System.out.println(user);
        userRepository.save(user);
        return "Inserted";
    }

    public String verifyUser(String token) {
        Optional<User> optionUser = userRepository.findByVerificationCode(token);
        if (optionUser.isPresent()) {
            User oldUser = optionUser.get();
            updateUser(oldUser, 0);

            return "Success";
        }
        return "Failed";
    }
}
