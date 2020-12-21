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
import com.mcc40.crud.entities.MyUserDetails;
import com.mcc40.crud.entities.Role;
import com.mcc40.crud.entities.Status;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.entities.data.RegisterData;
import com.mcc40.crud.jwt.JwtUtil;
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
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mochamad Yusuf
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MyUserDetailsService userDetailsService;
    private final AuthenticationManager authManager;

    @Autowired
    public UserService(UserRepository userRepository, EmployeeRepository employeeRepository, NotificationService notificationService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, MyUserDetailsService userDetailsService, AuthenticationManager authManager) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authManager = authManager;
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

    public String createJwtToken(String username, String password)
            throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);

        if (optionalUser.isPresent()) {                                 // User exist
            User user = optionalUser.get();
            Integer userStatus = user.getStatus().getId();
            if (comparePassword(user, password)) {   // Comparing password
                switch (userStatus) {
                    case -1:
                        throw new DisabledException("Unverified email login");
                    case 0:
                    case 1:
                    case 2:

                        userStatus = 0;
                        user.setStatus(new Status(0));
                        userRepository.save(user);

                        MyUserDetails userDetails
                                = new MyUserDetails(user);

                        return jwtUtil.generateToken(userDetails);
                    case 3:
                        throw new LockedException("User banned");
                    default:
                        throw new AuthenticationException("Unknown error") {
                        };
                }
            } else {
                switch (userStatus) {
                    case 0:
                    case 1:
                        userStatus++;
                        user.setStatus(new Status(userStatus));
                        userRepository.save(user);

                        throw new BadCredentialsException("Wrong password");

                    case 2:
                        userStatus++;
                        user.setStatus(new Status(userStatus));
                        userRepository.save(user);
                    default:
                        throw new LockedException("User banned");
                }
            }

        } else {
            throw new UsernameNotFoundException("No username or email registered");
        }
    }

    public String refreshToken(Authentication authentication) {
        return jwtUtil.generateToken((MyUserDetails) userDetailsService.loadUserByUsername(authentication.getName()));
    }

    public UsernamePasswordAuthenticationToken authenticate(String username, String password)
            throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);

        if (optionalUser.isPresent()) {                                 // User exist
            User user = optionalUser.get();
            Integer userStatus = user.getStatus().getId();
            if (comparePassword(user, password)) {   // Comparing password
                switch (userStatus) {
                    case -1:
                        throw new DisabledException("Unverified email login");
                    case 0:
                    case 1:
                    case 2:

                        userStatus = 0;
                        user.setStatus(new Status(0));
                        userRepository.save(user);

                        UsernamePasswordAuthenticationToken token
                                = new UsernamePasswordAuthenticationToken(
                                        user.getUserName(),
                                        user.getPassword(),
                                        user.getRoleList()
                                );

                        return token;
                    case 3:
                        throw new LockedException("User banned");
                    default:
                        throw new AuthenticationException("Unknown error") {
                        };
                }
            } else {
                switch (userStatus) {
                    case 0:
                    case 1:
                        userStatus++;
                        user.setStatus(new Status(userStatus));
                        userRepository.save(user);

                        throw new BadCredentialsException("Wrong password");

                    case 2:
                        userStatus++;
                        user.setStatus(new Status(userStatus));
                        userRepository.save(user);
                    default:
                        throw new LockedException("User banned");
                }
            }

        } else {
            throw new UsernameNotFoundException("No username or email registered");
        }
    }

    public Map<String, Object> login(String username, String password) {
        Map map = new LinkedHashMap();
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);

        try {
            UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(username, password);
            Authentication auth = authManager.authenticate(authReq);
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
            map.put("description", "User logged");

            map.put("user", obj);

            return map;
        } catch (UsernameNotFoundException unfe) {
            map.put("status", -1);
            map.put("description", unfe.getMessage());
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
        } catch (AuthenticationException ae) {
            map.put("status", optionalUser.get().getStatus().getId());
            map.put("description", ae.getMessage());
            return map;
        }
    }

    public Map<String, Object> register(RegisterData data) {
        Map status = new HashMap();

        String email = data.getEmail();

        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);

        if (!optionalEmployee.isPresent()) {
            System.out.println("create new employee");
            Employee employee = new Employee();
            employee.setId(employeeRepository.getAvailableId().get(0));
            employee.setFirstName(data.getFirstName());
            employee.setLastName(data.getLastName());
            employee.setEmail(email);
            employee.setPhoneNumber(data.getPhoneNumber());
            employee.setHireDate(data.getHireDate());
            employee.setSalary(data.getSalary());
            employee.setCommissionPct(data.getCommissionPct());

            Job job = new Job();
            job.setId(data.getJob());
            employee.setJob(job);

            Employee manager = new Employee();
            manager.setId(data.getManager());

            employee.setManager(manager);

            Department department = new Department();
            department.setId(data.getDepartment());
            employee.setDepartment(department);
            System.out.println(employee);

            employee = employeeRepository.saveAndFlush(employee);

            optionalEmployee = Optional.of(employee);
        }

        Integer id = optionalEmployee.get().getId();
        String username = data.getUsername();
        String password = data.getPassword();

        System.out.println(id + " | " + username + " | " + password);

        if (userRepository.findByEmail(username).isPresent()) {
            status.put("status", 403);
            status.put("description", "user is already registered");
            return status;
        }

        if (userRepository.findByUserName(username).isPresent()) {
            status.put("status", 403);
            status.put("description", "username is not available");
            return status;
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

    public Map<String, Object> changePassword(Authentication authentication, Map<String, String> json) {
        Map response = new LinkedHashMap();
        String oldPassword = json.get("oldPassword");
        String newPassword = json.get("newPassword");
        String retypePassword = json.get("retypePassword");
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(authentication.getName());
        if (!optionalUser.isPresent()) {
            response.put("status", 403);
            response.put("description", "no logged user");
            return response;
        }

        User user = optionalUser.get();

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

}
