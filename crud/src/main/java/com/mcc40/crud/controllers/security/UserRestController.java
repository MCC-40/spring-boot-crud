/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.security;

import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Role;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.services.EmployeeService;
import com.mcc40.crud.services.RoleService;
import com.mcc40.crud.services.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mochamad Yusuf
 */
@RestController
@RequestMapping("users")
public class UserRestController {

    UserService userService;
    EmployeeService employeeService;
    RoleService roleService;

    @Autowired
    public UserRestController(UserService userService, EmployeeService employeeService, RoleService roleService) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.roleService = roleService;
    }

    @GetMapping("")
    public ResponseEntity<List<Map<String, Object>>> searchUser(String keyword) {
        List<User> userList = userService.getAllUsers();
        if (keyword != null) {
            userList = userList.stream().filter(d
                    -> d.getId().toString().contains(keyword)
                    || d.getUserName().toString().contains(keyword)
            ).collect(Collectors.toList());
        }
        List<Map<String, Object>> mappedUserList = new ArrayList<>();
        for (User user : userList) {
            Map<String, Object> mapUser = new LinkedHashMap<>();
            mapUser.put("id", user.getId());
            mapUser.put("userName", user.getUserName());
            mapUser.put("password", user.getPassword());
            mappedUserList.add(mapUser);
        }

        if (userList.size() > 0) {
            return ResponseEntity.status(200).body(mappedUserList);
        } else {
            return ResponseEntity.status(404).build();
        }
    }


    @PostMapping("")
    public ResponseEntity<Map<String, String>> userSave(@RequestBody User user) {
        System.out.println(user);
        Map status = new HashMap();

        String result = userService.insertUser(user);
        status.put("Status", result);
        if (result.equals("Inserted") || result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @PutMapping("")
    public ResponseEntity<Map<String, String>> userSaveWithPut(@RequestBody User user) {
        System.out.println(user);
        Map status = new HashMap();

        String result = userService.putUser(user);
        status.put("Status", result);
        if (result.equals("Inserted") || result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<Map<String, String>> deleteUserById(int id) {
        Map status = new HashMap();
        if (userService.deleteUser(id)) {
            status.put("Status", "Success");
            return ResponseEntity.accepted().body(status);
        } else {
            status.put("Status", "Failed");
            return ResponseEntity.status(500).body(status);
        }
    }

    @PostMapping("login")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody Map<String, String> json) {
        Map status = new LinkedHashMap();
        String username = json.get("username");
        String password = json.get("password");

        List<User> userList = userService.getAllUsers();
        if (username != null) {
            userList = userList.stream().filter(d
                    -> d.getUserName().toString().equals(username)
                    || d.getEmployee().getEmail().toString().equals(username)
            ).collect(Collectors.toList());
        } else {
            status.put("status", "insert username!");
            return ResponseEntity.status(500).body(status);
        }

        if (userList.isEmpty()) {
            status.put("status", "no username or email match");
            return ResponseEntity.status(500).body(status);
        } else if (userList.size() == 1) {
            status.put("status", "logged in");
            User user = userList.get(0);
            status.put("id", user.getId());
            status.put("email", user.getEmployee().getEmail());

            List<String> roles = new ArrayList<>();
            for (Role role : user.getRoleList()) {
                roles.add(role.getName());
            }
            status.put("role", roles);
            return ResponseEntity.accepted().body(status);
        } else {
            status.put("status", "posible duplicate email");
            return ResponseEntity.status(500).body(status);
        }
    }

    @PostMapping("reg")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody Map<String, String> json,
            @RequestBody Employee employee) {
        String id = json.get("id");
        String username = json.get("username");
        String password = json.get("password");

        System.out.println(id + " | " + username + " | " + password);
        Map status = new HashMap();

        System.out.println((userService == null) + " 151");

        userService.test();

        if (userService.getUserById(Integer.parseInt(id)) != null) {
            status.put("status", "user is already registered");
            return ResponseEntity.status(500).body(status);
        }

        if (userService.getUserByUsername(username) != null) {
            status.put("status", "username is not available");
            return ResponseEntity.status(500).body(status);
        }

        if (employeeService.getByIdEmployee(Integer.parseInt(id)) == null) {
//            status.put("status", "creating employee");
            employeeService.saveEmployee(employee);
//            return ResponseEntity.status(500).body(status);
        }

//        if (roleService.getByIdRole(Integer.parseInt(role)) == null) {
//            status.put("status", "invalid role");
//            return ResponseEntity.status(500).body(status);
//        }
        User user = new User();
        user.setId(Integer.parseInt(id));
        user.setUserName(username);
        user.setPassword(password);

        userService.saveUser(user);

        List<Role> roleList = new ArrayList<>();
        roleList.add(roleService.getByIdRole(3));
        user.setRoleList(roleList);

        userService.saveUser(user);

        status.put("status", "success");

        return ResponseEntity.accepted().body(status);
    }

}
