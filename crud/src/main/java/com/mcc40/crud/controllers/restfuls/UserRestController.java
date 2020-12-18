/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.services.NotificationService;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.services.UserService;
import com.mcc40.crud.entities.data.RegisterData;
import com.mcc40.crud.repositories.UserRepository;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author asus
 */
//@RestController
@RestController
@RequestMapping("api/users")
public class UserRestController {
    
    UserService service;
    NotificationService notificationService;
    UserRepository userRepository;
    
    @Autowired
    public UserRestController(UserService service, NotificationService notificationService, UserRepository userRepository) {
        this.service = service;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    //get all
    @GetMapping("") //localhost:8081/
    public ResponseEntity<List<User>> registerDataGetAll() {
        return ResponseEntity.status(200).body(service.getAllUsers());
    }

    //get by keyword
    @GetMapping("search") 
    public ResponseEntity<List<User>> getUser(String keyword) {
        List<User> registerDatas = service.getAllUsers();
        List<User> result = (List<User>) registerDatas
                .stream()
                .filter((registerData) ->
                        registerData.getId().toString().contains(keyword) ||
                        registerData.getUsername().contains(keyword) ||
                        registerData.getEmployee().getEmail().contains(keyword)
                ).collect(Collectors.toList());
        
        return ResponseEntity.status(200).body(result);
    }

    @DeleteMapping("delete") //by id
    public String deleteUserById(int id) {
        User registerData = new User();
        registerData = service.getUserById(id);
        if(registerData.getId().equals(id)){
            service.deleteById(id);
            return "Delete Success";
        }
        return "Delete Failed";
    }  
    
    @PostMapping("register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterData registerData) throws MessagingException{
        return service.register(registerData);
    }
    
    @PostMapping("login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> input){
        
        String username = input.get("username");
        String password = input.get("password");
        
        Map status = new HashMap();
        
        status = service.login(username, password);
        
        return ResponseEntity.accepted().body(status);
    }
    
    @RequestMapping("mail")
    public String sendMail(){
        if(notificationService.javaSimpleEmail("manitihasibuan08@gmail.com")){
            return "index";
        }else
            return "index";
    }
    
    @RequestMapping("verify/{verificationCode}")
    public ResponseEntity<Map<String, Object>> verify(@PathVariable String verificationCode){
        Map status = new HashMap();
        status.put("Status", service.verify(verificationCode));
        
        return ResponseEntity.accepted().body(status);
    }
    
    //before login
    //localhost:8082/forgot-password/verificationCode *CLIENT SIDE!!*
    @PostMapping("forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> forgotPassword) throws MessagingException{
        Map status = new HashMap();
        String email = forgotPassword.get("email");
        boolean result = service.requestNewPassword(email);
        
        if(result){
            status.put("Status", "Link reset sended");
            return ResponseEntity.ok(status);
        }else
            status.put("Status", "Email not found");
            return ResponseEntity.status(500).body(status);
    }
    
    //reset password untuk lupa password (before login)
    @PostMapping("reset-password/{verificationCode}")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable String verificationCode, @RequestBody Map<String, String> resetPassword){
        Map status = new LinkedHashMap();
        String password = resetPassword.get("password");
        
        System.out.println(password);
        
        if(service.forgotPassword(verificationCode, password)){
            status.put("Status", "Password Changed");
            return ResponseEntity.accepted().body(status);
        }else
            status.put("Status", "Invalid new password");
        return ResponseEntity.status(500).body(status);
    }
    
//    @PostMapping("reset-password")
//    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> resetPassword){
//        Map status = new LinkedHashMap();
//        User user = service.getUserById(Integer.valueOf(resetPassword.get("id")));
//        String password = resetPassword.get("password");
//        System.out.println(password);
//        if(user != null){
//            user.setPassword(resetPassword.get("password"));
//            userRepository.saveAndFlush(user);
//            status.put("Status", "Password Changed");
//            return ResponseEntity.accepted().body(status);
//        }else
//            status.put("Status", "Invalid new password");
//        return ResponseEntity.status(500).body(status);
//    }
    
    //reset password untuk ganti password (after login)
    @PostMapping("reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(Authentication authentication, @RequestBody Map<String, String> resetPassword){
        Map map = service.resetPassword(authentication, resetPassword);
        Integer status = (Integer) map.get("Status");
        return ResponseEntity.status(status).body(map);
    }
}
