/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.security;

import com.mcc40.crud.entities.Department;
import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.Job;
import com.mcc40.crud.entities.Role;
import com.mcc40.crud.entities.Status;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.services.EmployeeService;
import com.mcc40.crud.services.NotificationService;
import com.mcc40.crud.services.RoleService;
import com.mcc40.crud.services.UserService;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("users")
public class UserRestController {

    UserService userService;
    EmployeeService employeeService;
    RoleService roleService;
    NotificationService notificationService;

    static User loggedUser;

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

        if (userList.size() == 1) {                                 // User exist
            User user = userList.get(0);
            if (userList.get(0).getPassword().equals(password)) {   // Comparing password
                switch (user.getStatus().getId()) {
                    case -1:
                        status.put("status", "unverified email log in");
                        break;
                    case 0:
                    case 1:
                    case 2:
                        loggedUser = user;
                        status.put("id", user.getId());
                        status.put("email", user.getEmployee().getEmail());

                        List<String> roles = new ArrayList<>();
                        for (Role role : user.getRoleList()) {
                            roles.add(role.getName());
                        }
                        status.put("role", roles);
                        break;
                    case 3:
                        status.put("status", "user banned");
                    default:
                        status.put("status", "invalid status");
                }
            } else {
                switch (user.getStatus().getId()) {
                    case 0:
                    case 1:
                    case 2:
                        user.setStatus(new Status(user.getStatus().getId() + 1));
                        userService.saveUser(user);
                        status.put("status", "wrong password");
                        break;
                    default:
                        status.put("status", "user banned");
                        break;
                }
            }

            return ResponseEntity.accepted().body(status);

        } else {
            status.put("status", "no username or email match");
            return ResponseEntity.status(500).body(status);
        }
    }

    @GetMapping("active")
    public ResponseEntity<Map<String, Object>> getActiveUser() {
        Map status = new LinkedHashMap();
        if (loggedUser != null) {
            status.put("id", loggedUser.getId());
            status.put("email", loggedUser.getEmployee().getEmail());

            List<String> roles = new ArrayList<>();
            for (Role role : loggedUser.getRoleList()) {
                roles.add(role.getName());
            }
            status.put("role", roles);
            return ResponseEntity.ok(status);
        } else {
            status.put("status", "no active user");
            return ResponseEntity.status(401).body(status);
        }

    }

    @PostMapping("reg")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody Map<String, String> json) throws InterruptedException {
        String id = json.get("id");
        String username = json.get("username");
        String password = json.get("password");

        System.out.println(id + " | " + username + " | " + password);
        Map status = new HashMap();

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

            System.out.println(employeeService.saveEmployee(employee));
        }

        User user = new User();
        user.setId(Integer.parseInt(id));
        user.setUserName(username);
        user.setPassword(password);
        user.setStatus(new Status(-1));
        user.setVerificationCode(UUID.randomUUID().toString());

        List<Role> roleList = new ArrayList<>();
        roleList.add(roleService.getByIdRole(3));
        user.setRoleList(roleList);

        userService.saveUser(user);

        List<User> userList = userService.getAllUsers();
        for (User user1 : userList) {
            System.out.println(user1.getEmployee().getEmail());
        }

        notificationService.javaSimpleEmail(user,
                "[Test] Verify your account",
                "Verify your account: " + "Localhost:8081/users/verify/" + user.getVerificationCode());

        status.put("status", "success");

        return ResponseEntity.accepted().body(status);
    }

    @GetMapping("verify/{verificationCode}")
    public ResponseEntity<Map<String, String>> verify(@PathVariable String verificationCode) {
        Map status = new HashMap();
        if (userService.verify(verificationCode)) {
            status.put("status", "verification success");
        } else {
            status.put("status", "verification failed");
        }
        return ResponseEntity.accepted().body(status);
    }

}
