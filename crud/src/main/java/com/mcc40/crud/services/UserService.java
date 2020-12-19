/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.MyUserDetails;
import com.mcc40.crud.entities.Role;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.entities.UserStatus;
import com.mcc40.crud.entities.auth.AuthenticationRequest;
import com.mcc40.crud.repositories.UserRepository;
import com.mcc40.crud.security.JwtUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yoshua
 */
@Service
public class UserService {

    private static UserRepository userRepository;
    private static PasswordEncoder encoder;
    private static AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder encoder, AuthenticationManager authenticationManager) {
        UserService.userRepository = userRepository;
        UserService.encoder = encoder;
        UserService.authenticationManager = authenticationManager;

    }

    public User getUserById(int id) {
        return userRepository.findById(id).get();
    }

    public static void changeStatusWrongCredential(String username) {
        User user = userRepository.findByUsername(username).get();
        updateUser(user, user.getStatus().getId() + 1);
    }

    public static void changeStatusLoginSuccess(String username) {
        User user = userRepository.findByUsername(username).get();
        updateUser(user, 0);
    }

    public boolean isUserLocked(int userStatus) {
        if (userStatus == -1) {
            throw new LockedException("User not verified");
        }
        if (userStatus == 3) {
            throw new LockedException("User Banned");
        }

        return false;
    }

    private static void updateUser(User oldUser, int statusId) {
        List<Role> roles = new ArrayList<>();
        oldUser.getRoles().forEach((role) -> {
            Role r = new Role();
            r.setId(role.getId());
            roles.add(r);
        });

        UserStatus status = new UserStatus();
        status.setId(statusId);

        User user = new User();
        user.setId(oldUser.getId());
        user.setUsername(oldUser.getUsername());
        user.setPassword(oldUser.getPassword());
        user.setVerificationCode(null);
        user.setRoles(roles);
        user.setStatus(status);

        userRepository.save(user);
    }

    public User register(Map<String, Object> data) {
        //Initialize Role
        Role role = new Role();
        role.setId(3);
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        //Initialize status
        UserStatus status = new UserStatus();
        status.setId(-1);

        User user = new User();
        user.setId(Integer.parseInt(data.get("id").toString()));
        user.setUsername(data.get("username").toString());
        user.setPassword(encoder.encode(data.get("password").toString()));
        user.setVerificationCode(UUID.randomUUID().toString());
        user.setRoles(roles);
        user.setStatus(status);

        userRepository.save(user);
        return user;
    }

    public String verifyUser(String token) {
        Optional<User> optionUser = userRepository.findByVerificationCode(token);
        if (optionUser.isPresent()) {
            User oldUser = optionUser.get();
            updateUser(oldUser, 0);

            return "Success";
        }
        return "Failed";
    }

    public User findUserByEmailFP(String email) {
        User user = userRepository.findByEmail(email).get();
        user.setVerificationCode(UUID.randomUUID().toString());
        userRepository.save(user);
        return user;
    }

    public void resetPassword(String verificationCode, String password) {
        User user = userRepository.findByVerificationCode(verificationCode).get();
        user.setPassword(password);
        user.setVerificationCode(null);
        userRepository.save(user);
    }

    public String resetPassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username).get();
        if (encoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(encoder.encode(newPassword));
            userRepository.save(user);
            return "Success";
        }
        return "Wrong Last Password";
    }

    public String createAuthenticationToken(AuthenticationRequest authenticationRequest) throws Exception {
        MyUserDetails userDetails = (MyUserDetails) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String username = authenticationRequest.getUsername();

        if (!isUserLocked(userDetails.getStatusCode())) {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, authenticationRequest.getPassword(), new ArrayList<>())
                );
                changeStatusLoginSuccess(username);
            } catch (BadCredentialsException e) {
                changeStatusWrongCredential(username);
                throw new Exception("Incorrect username or password", e);
            }
        }

        return jwtUtil.generateToken(userDetails);
    }

    public String refreshToken(Authentication authentication) {
        return jwtUtil.generateToken((MyUserDetails) userDetailsService.loadUserByUsername(authentication.getName()));
    }

    private static Map<String, Object> loginResultSetup(User user) {
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        List<String> listRole = new ArrayList<>();
        user.getRoles().forEach((role) -> {
            listRole.add(role.getName());
        });
        userMap.put("roles", listRole);
        userMap.put("email", user.getEmployee().getEmail());
        result.put("description", userMap);
        result.put("status", 200);
        updateUser(user, 0);
        return result;
    }

    public Map<String, Object> login(String usernameOrEmail) {
        User user = userRepository.findByUsername(usernameOrEmail).get();
        Map<String, Object> result = loginResultSetup(user);
        updateUser(user, 0);
        return result;
    }
}
