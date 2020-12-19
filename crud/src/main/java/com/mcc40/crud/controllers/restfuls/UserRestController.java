/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.User;
import com.mcc40.crud.entities.auth.AuthenticationRequest;
import com.mcc40.crud.entities.auth.AuthenticationResponse;
import com.mcc40.crud.services.EmployeeService;
import com.mcc40.crud.services.NotificationService;
import com.mcc40.crud.services.UserService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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

    private final UserService service;
    private final EmployeeService employeeService;
    private final NotificationService notificationService;

    @Autowired
    public UserRestController(UserService service, NotificationService notificationService, EmployeeService employeeService) {
        this.service = service;
        this.notificationService = notificationService;
        this.employeeService = employeeService;
    }

    @PostMapping("login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        String jwt = service.createAuthenticationToken(authenticationRequest);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @GetMapping("refresh-token")
    public ResponseEntity<?> refreshToken(Authentication authentication) throws Exception {
        String jwt = service.refreshToken(authentication);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody Map<String, Object> data) {
        User user = service.register(data);
        notificationService.sendVerificationMail(user.getId(), user.getVerificationCode());
        return ResponseEntity.accepted().body("Verfication Email Send");

    }

    @PostMapping("register/employee")
    public ResponseEntity<String> registerNewEmployee(@RequestBody Map<String, Object> data) {
        employeeService.registerEmployee((Map<String, Object>) data.get("user"));
        User user = service.register((Map<String, Object>) data.get("employee"));
        notificationService.sendVerificationMail(user.getId(), user.getVerificationCode());
        return ResponseEntity.accepted().body("Inserted");
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
        notificationService.sendForgotPasswordMail(email, service.findUserByEmailFP(email).getVerificationCode());
        return ResponseEntity.ok("Reset Password Mail has been send");
    }

    @PostMapping("reset-password")
    public ResponseEntity<String> resetPasswordForgetPassword(@RequestBody HashMap<String, String> data) {
        service.resetPassword(data.get("token"), data.get("password"));
        return ResponseEntity.accepted().body("Success");
    }

    @PutMapping("reset-password")
    public ResponseEntity<String> resetPassword(Authentication authentication, @RequestBody HashMap<String, String> data) {
        String result = service.resetPassword(authentication.getName(), data.get("oldPassword"), data.get("newPassword"));
        return ResponseEntity.accepted().body(result);
    }
}
