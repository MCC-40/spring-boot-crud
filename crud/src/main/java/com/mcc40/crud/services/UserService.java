/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Role;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.EmployeeRepository;
import com.mcc40.crud.repositories.RoleRepository;
import com.mcc40.crud.repositories.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yoshua
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public UserService(UserRepository userRepository, EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    public Map<String, Object> login(String usernameOrEmail, String password) {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "Not Found");
        Optional<User> optionalUser = userRepository.findByUsername(usernameOrEmail);
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(usernameOrEmail);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(password)) {
                result.clear();
                result.put("id", user.getId());
                List<String> listRole = new ArrayList<>();
                user.getRoles().forEach((role) -> {
                    listRole.add(role.getName());
                });
                result.put("roles", listRole);
                result.put("email", user.getEmployee().getEmail());
            }
        } else if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            if (employee.getEmail().equals(usernameOrEmail) && employee.getUser().getPassword().equals(password)) {
                result.clear();
                result.put("id", employee.getUser().getId());
                List<String> listRole = new ArrayList<>();
                employee.getUser().getRoles().forEach((role) -> {
                    listRole.add(role.getName());
                });
                result.put("roles", listRole);
                result.put("email", employee.getEmail());
            }
        }
        return result;
    }

    public String register(User user) {
        Role role = new Role();
        role.setId(3);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
        return "Inserted";
    }

//    public void saveRole(Role role) {
//        roleRepository.save(role);
//    }
}
