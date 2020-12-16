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
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Yoshua
 */
@RestController
@RequestMapping("api/user")
public class UserRestController {

    private UserService service;
    private NotificationService notificationService;

    @Autowired
    public UserRestController(UserService service, NotificationService notificationService) {
        this.service = service;
        this.notificationService = notificationService;
    }

    @GetMapping("verify")
    public ResponseEntity<Map<String, String>> verifyUser(String token) {
        Map status = new HashMap();
        status.put("Status", service.verifyUser(token));
        return ResponseEntity.ok(status);
    }

    @PostMapping("forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody Map<String, Object> data) {
        String email = data.get("email").toString();
        User user = service.findUserByEmailFP(email);
        notificationService.sendForgotPasswordMail(email, user.getVerificationCode());
        return ResponseEntity.accepted().body("Reset Password Mail has been send");
    }

    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody HashMap<String, String> data) {
        service.resetPassword(data.get("token"), data.get("password"));
        return ResponseEntity.accepted().body("Success");
    }

    @PutMapping("reset-password")
    public ResponseEntity<String> resetPassword2(Authentication authentication, @RequestBody HashMap<String, String> data) {
        if (authentication.isAuthenticated()) {
            String result = service.resetPassword(authentication.getName(), data.get("oldPassword"), data.get("newPassword"));
            return ResponseEntity.accepted().body(result);
        }
        return ResponseEntity.accepted().body("Not Authenticated");
    }
}
