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
import com.mcc40.crud.services.EmployeeService;
import com.mcc40.crud.services.NotificationService;
import com.mcc40.crud.services.UserService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private EmployeeService employeeService;
    private NotificationService notificationService;

    @Autowired
    public UserRestController(UserService service, NotificationService notificationService, EmployeeService employeeService) {
        this.service = service;
        this.notificationService = notificationService;
        this.employeeService = employeeService;
    }

    @PostMapping("login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> data) {
        String usernameOrEmail = data.get("usernameOrEmail");
        String password = data.get("password");
        Map<String, Object> result = service.login(usernameOrEmail, password);
        return ResponseEntity.status(Integer.parseInt(result.get("status").toString())).body(result.get("description"));
    }

    @PostMapping("register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> data) {
        Map status = new HashMap();
        User user = service.register(data);
        notificationService.sendVerificationMail(user.getId(), user.getVerificationCode());
        status.put("Status", "Verfication Email Send");
        return ResponseEntity.accepted().body(status);

    }

    @PostMapping("register/employee")
    public ResponseEntity<Map<String, String>> registerNewEmployee(@RequestBody Map<String, Object> data) {
        Map status = new HashMap();
        status.put("Status: ", "Inserted");
        Map<String, Object> user = (Map<String, Object>) data.get("user");
        Map<String, Object> employee = (Map<String, Object>) data.get("employee");
        employeeService.registerEmployee(employee);
        service.register(user);
        return ResponseEntity.accepted().body(status);
    }

    @GetMapping("user/verify")
    public ResponseEntity<Map<String, String>> verifyUser(String token) {
        Map status = new HashMap();
        status.put("Status", service.verifyUser(token));
        return ResponseEntity.ok(status);
    }
}
