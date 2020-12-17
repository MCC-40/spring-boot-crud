/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.User;
import com.mcc40.crud.services.EmployeeService;
import com.mcc40.crud.services.NotificationService;
import com.mcc40.crud.services.RoleService;
import com.mcc40.crud.services.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mochamad Yusuf
 */
@RestController
@RequestMapping("api/users")
public class UserRestController {

    UserService userService;
    EmployeeService employeeService;
    RoleService roleService;
    NotificationService notificationService;

    @Autowired
    public UserRestController(UserService userService,
            EmployeeService employeeService,
            RoleService roleService,
            NotificationService notificationService) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.notificationService = notificationService;
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

    @PostMapping("login")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody Map<String, String> data) {
        Map map = userService.login(data.get("username"), data.get("password"));

        if (map.get("status").equals(0)) {
            return ResponseEntity.ok(map);
        } else {
            return ResponseEntity.status(401).body(map);
        }
    }

    @PostMapping("register")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody Map<String, String> json) throws InterruptedException {
        Map response = new HashMap();
        response = userService.register(json);
        Integer status = (Integer) response.get("status");
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("verify/{verificationCode}")
    public ResponseEntity<Map<String, String>> verify(@PathVariable String verificationCode) {
        Map status = new HashMap();
        if (userService.verify(verificationCode)) {
            status.put("status", "verification success");
            return ResponseEntity.accepted().body(status);
        } else {
            status.put("status", "invalid verification request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(status);
        }

    }

    @PostMapping("reset-password/{verificationCode}")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable String verificationCode,
            @RequestBody Map<String, String> json) {
        Map status = new LinkedHashMap();

        String password = json.get("password");
        if (userService.resetPassword(verificationCode, password)) {
            status.put("status", "password changed");
            return ResponseEntity.accepted().body(status);
        } else {
            status.put("status", "invalid password reset request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(status);
        }
    }

    @PostMapping("forgot-password")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@RequestBody Map<String, String> json) {
        Map map = new LinkedHashMap();
        String email = json.get("email");
        boolean result = userService.requestPasswordReset(email);
        if (result) {
            map.put("Status", "Reset link sent");
            return ResponseEntity.ok(map);
        } else {
            map.put("Status", "No email found");
            return ResponseEntity.status(500).body(map);
        }

    }

    @PostMapping("change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> json,
            Authentication authentication) {
        Map map = userService.changePassword(authentication, json);
        Integer status = (Integer) map.get("status");
        return ResponseEntity.status(status).body(map);
    }

}
