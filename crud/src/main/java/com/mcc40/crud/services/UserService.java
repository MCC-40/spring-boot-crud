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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yoshua
 */
@Service
public class UserService {

    private static UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private static PasswordEncoder encoder;
    
    @Autowired
    public UserService(UserRepository userRepository, EmployeeRepository employeeRepository, PasswordEncoder encoder) {
        UserService.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.encoder = encoder;

    }

    public User getUserById(int id) {
        return userRepository.findById(id).get();
    }

    private static void updateUser(User olduUser, int statusId) {
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
    }

    private static Map<String, Object> loginResultSetup(User user, String password) {
        Map<String, Object> result = new HashMap<>();

        if (user.getStatus().getId() == -1) {
            result.put("description", "User not Verified");
            result.put("status", 401);
        } else if (user.getStatus().getId() == 3) {
            result.put("description", "User Banned");
            result.put("status", 401);
        } else if (encoder.matches(password, user.getPassword())) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            List<String> listRole = new ArrayList<>();
            user.getRoles().forEach((role) -> {
                listRole.add(role.getName());
            });
            userMap.put("roles", listRole);
            userMap.put("email", user.getEmployee().getEmail());
            result.put("description", userMap);
            result.put("status", 200);
            updateUser(user, 0);
        } else {
            result.put("description", "Wrong Password");
            result.put("status", 401);
            updateUser(user, user.getStatus().getId() + 1);
        }
        return result;
    }

    public Map<String, Object> login(String usernameOrEmail, String password) {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "Not Found");
        Optional<User> optionalUser = userRepository.findByUsername(usernameOrEmail);
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(usernameOrEmail);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            result.clear();
            result = loginResultSetup(user, password);
        } else if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            User user = employee.getUser();
            result.clear();
            result = loginResultSetup(user, password);
        }
        return result;
    }

    public User register(Map<String, Object> data) {
        User user = new User();
        user.setId(Integer.parseInt(data.get("id").toString()));
        user.setUsername(data.get("username").toString());
        user.setPassword(encoder.encode(data.get("password").toString()));
        user.setVerificationCode(UUID.randomUUID().toString());

        Role role = new Role();
        role.setId(3);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        UserStatus status = new UserStatus();
        status.setId(-1);
        user.setStatus(status);

        userRepository.save(user);
        return user;
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

    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email).get();
        user.setVerificationCode(UUID.randomUUID().toString());
        userRepository.save(user);
        return user;
    }

    public void resetPassword(String verificationCode, String password) {
        User user = userRepository.findByVerificationCode(verificationCode).get();
        user.setPassword(password);
        user.setVerificationCode(null);
        userRepository.save(user);
    }

    public String resetPassword(int id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id).get();
        if (user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            userRepository.save(user);
            return "Success";
        }
        return "Failed";
    }
}
