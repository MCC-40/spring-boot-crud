/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Department;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.UserRepository;
import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Job;
import com.mcc40.crud.entities.Role;
import com.mcc40.crud.entities.Status;
import com.mcc40.crud.entities.data.RegisterData;
import com.mcc40.crud.repositories.EmployeeRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author asus
 */
@Service
public class UserService implements UserDetailsService{
    
    UserRepository userRepository;
    EmployeeRepository employeeRepository;
    NotificationService notificationService;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    
    @Autowired
    public UserService(UserRepository userRepository, 
            EmployeeRepository employeeRepository, 
            NotificationService notificationService, 
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        
    }
    
    //get All
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    
    //get by id
    public User getUserById(int id){
        return userRepository.findById(id).get();
    }
    
    //get by username
    public void getUserByUsername(String username){
        List<User> users = userRepository.findAll();
        for (User user : users) {
            System.out.println(user.getId() + " | " + user.getUsername());
        }
    }
    
    //get by email
    public User getUserByEmail(String username){
        Optional<User> users = userRepository.findByEmail(username);
        if(users.isPresent()){
            return users.get();
        }else
            return null;
    }
    
    public ResponseEntity<Map<String, String>> register(RegisterData registerData) throws MessagingException{
        Map status = new HashMap();
//        registerData.
        System.out.println(registerData);
        if(registerData.getId() == null){
            status.put("Status", "No Content");
            return ResponseEntity.status(200).body(status);
        }
        
        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        User user = new User();
        role.setId(3);
        roles.add(role);
        user.setRoleList(roles);
        user.setVerificationCode(UUID.randomUUID().toString());
        user.setId(registerData.getId());
        Employee employee = new Employee();
        employee.setId(registerData.getId());
        employee.setFirstName(registerData.getFirstName());
        employee.setLastName(registerData.getLastName());
        employee.setEmail(registerData.getEmail());
        employee.setPhoneNumber(registerData.getPhoneNumber());
        employee.setHireDate(registerData.getHireDate());
        
        Job job = new Job();
        job.setId(registerData.getJob());
        employee.setJob(job);
        
        employee.setSalary(registerData.getSalary());
        employee.setCommissionPct(registerData.getCommissionPct());
        
        Employee manager = new Employee();
        manager.setId(registerData.getManager());
        employee.setManager(manager);
        
        Department department = new Department();
        department.setId(registerData.getDepartment());
        employee.setDepartment(department);
        
        user.setUsername(registerData.getUsername());
        user.setPassword(passwordEncoder.encode(registerData.getPassword()));

        user.setEmployee(employee);
        user.setStatus(new Status(-1));
        
        notificationService.javaMimeMessage(user);
        
        String result = null;
        Optional<Employee> optionalEmployee = employeeRepository.findById(user.getId());
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if(optionalUser.isPresent() == false ){
            if(optionalEmployee.isPresent() == false){
                employeeRepository.save(user.getEmployee());
            }
            userRepository.saveAndFlush(user);
            result = "Registered";
        } else {
            result = "User already exist";
        }
        
        status.put("Status", result);
        
        return ResponseEntity.accepted().body(status);
        
    }
    
    public String verify(String verificationCode){
        userRepository.findByVerificationCode(verificationCode);
        Optional<User> optionalUser = userRepository.findByVerificationCode(verificationCode);
        if(optionalUser.isPresent()){
            optionalUser.get().setVerificationCode(null);
            optionalUser.get().setStatus(new Status(0));
            userRepository.save(optionalUser.get());
            return "verified";
        } else 
            return "failed";
    }
    
    public UsernamePasswordAuthenticationToken loginAuthentication(String username, String password){
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);

        if (optionalUser.isPresent()) {                                 // User exist
            User user = optionalUser.get();
            Integer userStatus = user.getStatus().getId();
            if (comparePassword(user, password)) {   // Comparing password
                switch (userStatus) {
                    case -1:
                        throw new DisabledException("Account has not been verified");
                    case 0:
                    case 1:
                    case 2:

                        userStatus = 0;
                        user.setStatus(new Status(0));
                        userRepository.save(user);

                        UsernamePasswordAuthenticationToken upat
                                = new UsernamePasswordAuthenticationToken(
                                        user.getUsername(),
                                        user.getPassword(),
                                        user.getRoleList()
                                );

                        return upat;
                    case 3:
                        throw new LockedException("Account banned");
                    default:
                        throw new AuthenticationException("Unknown exception") {
                        };
                }
            } else {
                switch (userStatus) {
                    case 0:
                    case 1:
                        userStatus++;
                        user.setStatus(new Status(userStatus));
                        userRepository.save(user);

                        throw new BadCredentialsException("Account rejected, wrong password");

                    case 2:
                        userStatus++;
                        user.setStatus(new Status(userStatus));
                        userRepository.save(user);
                    default:
                        throw new LockedException("Account banned");
                }
            }
        }else {
            throw new UsernameNotFoundException("No username or email registered");
        }
    }
    
    public Map<String, Object> login(String username, String password){
        Map map = new LinkedHashMap();
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);

        try {
            UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(username, password);
            Authentication auth = authenticationManager.authenticate(authReq);
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);

            User user = optionalUser.get();
            Map obj = new LinkedHashMap();

            obj.put("id", user.getId());
            obj.put("email", user.getEmployee().getEmail());

            List<String> roles = new ArrayList<>();
            for (Role role : user.getRoleList()) {
                roles.add(role.getName());
            }
            obj.put("role", roles);
            map.put("status", user.getStatus().getId());
            map.put("description", "user logged");

            map.put("user", obj);
        } catch (UsernameNotFoundException unfe) {
            map.put("status", -1);
            map.put("description", unfe.getMessage()); //unfe.getMessage()
            return map;
        } catch (DisabledException de) {
            map.put("status", optionalUser.get().getStatus().getId());
            map.put("status", de.getMessage());
            return map;
        } catch (BadCredentialsException bce) {
            map.put("status", optionalUser.get().getStatus().getId());
            map.put("description", bce.getMessage());
            return map;
        } catch (LockedException le) {
            map.put("status", optionalUser.get().getStatus().getId());
            map.put("description", le.getMessage());
            return map;
        }
        catch (AuthenticationException ae) {
            map.put("status", optionalUser.get().getStatus().getId());
            map.put("description", ae.getMessage());
        }

        return map;
    }
    
    public boolean deleteById(int id){
        userRepository.deleteById(id);
        return !userRepository.findById(id).isPresent();
    }
    
    //untuk lupa password untuk before login
    public boolean forgotPassword(String verificationCode, String password){
        Optional<User> optionalUser = userRepository.findByVerificationCode(verificationCode);
        
        if(optionalUser.isPresent()){
            if(optionalUser.get().getStatus().getId() != -1){
                optionalUser.get().setStatus(new Status(0));
                optionalUser.get().setPassword(passwordEncoder.encode(password));
                optionalUser.get().setVerificationCode(null);
                
                userRepository.save(optionalUser.get());
                
                return true;
            }
        }
        return false;
    }
     
    //lupa password untuk before login
    public boolean requestNewPassword(String email) throws MessagingException{
        User user = getUserByEmail(email);
        
        if(user != null){
            user.setVerificationCode(UUID.randomUUID().toString());
            userRepository.save(user);
            notificationService.javaMimeMessageForgotPassword(user);
            
            return true;
        }else
            return false;
    }
    
    
    //reset password untuk after login
    public Map<String, Object> resetPassword(Authentication authentication, @RequestBody Map<String, String> resetPassword){
        Map status = new LinkedHashMap();
        String oldPassword = resetPassword.get("oldPassword");
        String newPassword = resetPassword.get("newPassword");
        String retypePassword = resetPassword.get("retypePassword");
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(authentication.getName());
        if(!optionalUser.isPresent()){
            status.put("Status", 403);
            status.put("description", "Not login yet");
            return status;
        }
        
        User user = optionalUser.get();
        
        if(!comparePassword(user, oldPassword)){
            status.put("Status", 403);
            status.put("description", "Old password not match");
            return status;
        }
        
        if(!newPassword.equals(retypePassword)) {
            status.put("Status", 403);
            status.put("description", "New password not match");
            return status;
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.saveAndFlush(user);
        status.put("Status", 202);
        status.put("description", "Password changed");
        
        return status;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRoleList());
            return userDetails;
        } else {
            throw new UsernameNotFoundException("username not available in database");
        }
    }

    private boolean comparePassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }
    
}
