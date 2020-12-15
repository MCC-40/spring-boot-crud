/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.services.NotificationService;
import com.mcc40.crud.services.UserService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Yoshua
 */
@RestController
@RequestMapping("api")
public class UserRestController {

    private UserService service;
    private NotificationService notificationService;

    @Autowired
    public UserRestController(UserService service, NotificationService notificationService) {
        this.service = service;
        this.notificationService = notificationService;
    }

    @PostMapping("login")
    public ResponseEntity<Object> insertUser(@RequestBody ObjectNode objectNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        String usernameOrEmail = objectMapper.convertValue(objectNode.get("usernameOrEmail"), String.class);
        String password = objectMapper.convertValue(objectNode.get("password"), String.class);
        return ResponseEntity.accepted().body(service.login(usernameOrEmail, password));
    }

    @PostMapping("register")
    public ResponseEntity<Map<String, String>> insertUser(@Validated @RequestBody User user) {
        Map status = new HashMap();
        String result = service.register(user);
        if (result.equals("Inserted")) {
            notificationService.sendVerificationMail(user.getId());
            status.put("Status", "Verfication Email Send");

            return ResponseEntity.accepted().body(status);
        }
        return ResponseEntity.status(500).body(status);
    }

    @PostMapping("register/employee")
    public ResponseEntity<Map<String, String>> registerNewEmployee(@RequestBody ObjectNode objectNode) {
        Map status = new HashMap();
        status.put("Status: ", "Inserted");
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.convertValue(objectNode.get("user"), User.class);
        Employee employee = objectMapper.convertValue(objectNode.get("employee"), Employee.class);
        if (EmployeeRestController.registerEmployee(employee).equals("Inserted")) {
            String result = service.register(user);
            status.put("Status", result);
            if (result.equals("Inserted")) {
                return ResponseEntity.accepted().body(status);
            }
            return ResponseEntity.status(500).body(status);
        }

        status.put("Status: ", "Failed");
        return ResponseEntity.status(500).body(status);
    }

    @GetMapping("user/verify")
    public ResponseEntity<Map<String, String>> verifyUser(String token) {
        Map status = new HashMap();
        status.put("Status", service.verifyUser(token));
        return ResponseEntity.ok(status);
    }
}
