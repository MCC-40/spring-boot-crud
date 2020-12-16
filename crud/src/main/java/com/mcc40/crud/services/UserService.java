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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mochamad Yusuf
 */
@Service
public class UserService {

    UserRepository userRepository;
    EmployeeRepository employeeRepository;
    NotificationService notificationService;
    RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, EmployeeRepository employeeRepository, NotificationService notificationService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
        this.roleRepository = roleRepository;
    }

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
            if (user.getPassword().equals(password)) {   // Comparing password
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

        String id = json.get("id");
        String username = json.get("username");
        String password = json.get("password");

        System.out.println(id + " | " + username + " | " + password);

        if (!userRepository.findById(Integer.parseInt(id)).isPresent()) {
            status.put("status", 403);
            status.put("description", "user is already registered");
//            return ResponseEntity.status(500).body(status);
            return status;
        }

        if (!userRepository.findByUserName(username).isPresent()) {
            status.put("status", 403);
            status.put("description", "username is not available");
            return status;
        }

        if (!employeeRepository.findById(Integer.parseInt(id)).isPresent()) {
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
        user.setId(Integer.parseInt(id));
        user.setUserName(username);
        user.setPassword(password);
        user.setStatus(new Status(-1));
        user.setVerificationCode(UUID.randomUUID().toString());

        List<Role> roleList = new ArrayList<>();
        roleList.add(roleRepository.findById(3).get());
        user.setRoleList(roleList);

        userRepository.save(user);

        try {
            notificationService.sendEmail(user,
                    "Verify your account ",
                    "<html><body>"
                    + "Verify your account "
                    + "<a href='http://Localhost:8081/users/verify/" + user.getVerificationCode()
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
            userRepository.save(user.get());
            return true;
        } else {
            return false;
        }
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
            user.setPassword(password);
            user.setStatus(new Status(0));
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

}
