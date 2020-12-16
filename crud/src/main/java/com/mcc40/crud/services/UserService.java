/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.controllers.restfuls.UserRestController;
import com.mcc40.crud.entities.Department;
import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Job;
import com.mcc40.crud.entities.Role;
import com.mcc40.crud.entities.Status;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.EmployeeRepository;
import com.mcc40.crud.repositories.RoleRepository;
import com.mcc40.crud.repositories.UserRepository;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mochamad Yusuf
 */
@Service
public class UserService implements UserDetailsService {

    UserRepository userRepository;
    EmployeeRepository employeeRepository;
    NotificationService notificationService;
    RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, EmployeeRepository employeeRepository, NotificationService notificationService, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
//    @Autowired
//    public UserService(UserRepository userRepository, EmployeeRepository employeeRepository, NotificationService notificationService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authManager) {
//        this.userRepository = userRepository;
//        this.employeeRepository = employeeRepository;
//        this.notificationService = notificationService;
//        this.roleRepository = roleRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.authManager = authManager;
//    }

    //get all 
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //get by id
    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        System.out.println(user.isPresent());
        if (!user.isPresent()) {
            return null;
        } else {
            return user.get();
        }
    }

    public User getUserByUsername(String username) {
        Optional<User> users = userRepository.findByUserName(username);
        if (!users.isPresent()) {
            return null;
        } else {
            return users.get();
        }
    }

    public User getUserByEmail(String username) {
        Optional<User> users = userRepository.findByEmail(username);
        if (!users.isPresent()) {
            return null;
        } else {
            return users.get();
        }
    }

    public boolean comparePassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    public String saveUser(User user) {
        Optional<Employee> emp = employeeRepository.findById(user.getId());
        if (emp.isPresent()) {
            user.setEmployee(emp.get());
        }
        userRepository.save(user);
        return "success";
    }

    public Map<String, Object> login(String username, String password) {
        Map map = new LinkedHashMap();
        Map obj = new LinkedHashMap();
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);

        if (optionalUser.isPresent()) {                                 // User exist
            User user = optionalUser.get();
            Integer userStatus = user.getStatus().getId();
            if (comparePassword(user, password)) {   // Comparing password
                switch (userStatus) {
                    case -1:
                        map.put("status", "unverified email log in");
                        break;
                    case 0:
                    case 1:
                    case 2:
                        userStatus = 0;
                        user.setStatus(new Status(0));
                        userRepository.save(user);

//                        loggedUser = user;
                        obj.put("id", user.getId());
                        obj.put("email", user.getEmployee().getEmail());

                        List<String> roles = new ArrayList<>();
                        for (Role role : user.getRoleList()) {
                            roles.add(role.getName());
                        }
                        obj.put("role", roles);
                        map.put("status", userStatus);
                        map.put("user", obj);
                        map.put("entity", user);

//                        UsernamePasswordAuthenticationToken authReq
//                                = new UsernamePasswordAuthenticationToken(username, password);
//                        Authentication auth = authManager.authenticate(authReq);
//                        SecurityContext sc = SecurityContextHolder.getContext();
//                        sc.setAuthentication(auth);
//                        
//                        List<String> roleList = new ArrayList<>();
//                        for (GrantedAuthority authority : sc.getAuthentication().getAuthorities()) {
//                            roleList.add(authority.getAuthority());
//                        }
//                        System.out.println("auth: " + roleList);
                        break;
                    case 3:
                        map.put("status", userStatus);
                        map.put("description", "used banned");
                    default:
                        map.put("description", "unknown error");
                }
            } else {
                switch (userStatus) {
                    case 0:
                    case 1:
                    case 2:
                        userStatus++;
                        user.setStatus(new Status(userStatus));
                        userRepository.save(user);

                        map.put("status", userStatus);
                        map.put("description", "wrong password");
                        break;
                    default:
                        map.put("status", userStatus);
                        map.put("description", "used banned");
                        break;
                }
            }
            return map;

        } else {
            map.put("status", -1);
            map.put("description", "no username or email registered");
            return map;
        }
    }

    public Map<String, Object> register(Map<String, String> json) {
        Map status = new HashMap();

        Integer id = Integer.parseInt(json.get("id"));
        String username = json.get("username");
        String password = json.get("password");

        System.out.println(id + " | " + username + " | " + password);

        if (userRepository.findById(id).isPresent()) {
            status.put("status", 403);
            status.put("description", "user is already registered");
//            return ResponseEntity.status(500).body(status);
            return status;
        }

        if (userRepository.findByUserName(username).isPresent()) {
            status.put("status", 403);
            status.put("description", "username is not available");
            return status;
        }

        if (!employeeRepository.findById(id).isPresent()) {
//            status.put("status", "creating employee");
            System.out.println("create new employee");
            Employee employee = new Employee();
            employee.setId(Integer.parseInt(json.get("id")));
            employee.setFirstName(json.get("firstName"));
            employee.setLastName(json.get("lastName"));
            employee.setEmail(json.get("email"));
            employee.setPhoneNumber(json.get("phoneNumber"));
            employee.setHireDate(Date.valueOf(json.get("hireDate")));
            employee.setSalary(BigDecimal.valueOf(Long.parseLong(json.get("salary"))));
            if (json.get("commissionPct") != null) {
                employee.setCommissionPct(BigDecimal.valueOf(Long.getLong(json.get("commissionPct").toString())));
            } else {
                employee.setCommissionPct(null);
            }

            Job job = new Job();
            job.setId(json.get("jobId"));
            employee.setJob(job);

            Employee manager = new Employee();
            manager.setId(Integer.parseInt(json.get("managerId")));

            employee.setManager(manager);

            Department department = new Department();
            department.setId(Integer.parseInt(json.get("departmentId")));
            employee.setDepartment(department);
            System.out.println(employee);

            employeeRepository.save(employee);
        }

        User user = new User();
        user.setId(id);
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(new Status(-1));
        user.setVerificationCode(UUID.randomUUID().toString());

        List<Role> roleList = new ArrayList<>();
        roleList.add(roleRepository.findById(3).get());
        user.setRoleList(roleList);

        user.setEmployee(employeeRepository.findById(id).get());

        userRepository.save(user);

        try {
            notificationService.sendEmail(user,
                    "Verify your account ",
                    "<html><body>"
                    + "Verify your account "
                    + "<a href='http://Localhost:8081/api/users/verify/" + user.getVerificationCode()
                    + "'>here</a>"
                    + "</body></html>");
        } catch (MessagingException ex) {
            Logger.getLogger(UserRestController.class.getName()).log(Level.SEVERE, null, ex);
        }

        status.put("status", 202);
        status.put("description", "success");

        return status;
    }

    public boolean verify(String verificationCode) {
        Optional<User> user = userRepository.findByVerificationCode(verificationCode);
        if (user.isPresent()) {
            user.get().setStatus(new Status(0));
            user.get().setVerificationCode(null);
            userRepository.save(user.get());
            return true;
        } else {
            return false;
        }
    }

    public Map<String, Object> changePassword(User user, Map<String, String> json) {
        Map response = new LinkedHashMap();
        String oldPassword = json.get("oldPassword");
        String newPassword = json.get("newPassword");
        String retypePassword = json.get("retypePassword");

        if (user == null) {
            response.put("status", 403);
            response.put("description", "no logged user");
            return response;
        }

        if (!comparePassword(user, oldPassword)) {
            response.put("status", 403);
            response.put("description", "old password not match");
            return response;
        }

        if (!validatePassword(newPassword)) {
            response.put("status", 403);
            response.put("description", "new password invalid");
            return response;
        }

        if (!newPassword.equals(retypePassword)) {
            response.put("status", 403);
            response.put("description", "retyped password not match");
            return response;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        response.put("status", 202);
        response.put("description", "password changed");
        return response;
    }

    public boolean validatePassword(String password) {
        return password.length() >= 8;
    }

    public boolean requestPasswordReset(String email) {
        User user = getUserByEmail(email);
        if (user != null) {
            try {
                user.setVerificationCode(UUID.randomUUID().toString());     // Re generating verification code
                saveUser(user);

                notificationService.sendEmail(user,
                        "Reset password",
                        "<html><body>"
                        + "Reset your password "
                        + "<a href='http://Localhost:8082/users/forgot-password/" + user.getVerificationCode()
                        + "'>here</a>"
                        + "</body></html>");
            } catch (MessagingException ex) {
                Logger.getLogger(UserRestController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean resetPassword(String verificationCode, String password) {
        Optional<User> optionalUser = userRepository.findByVerificationCode(verificationCode);
        if (optionalUser.isPresent() && optionalUser.get().getStatus().getId() != -1) {
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(password));
            user.setStatus(new Status(0));
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getUserName(),
                    user.getPassword(),
                    user.getRoleList());
            return userDetails;
        } else {
            return null;
        }
    }

}
