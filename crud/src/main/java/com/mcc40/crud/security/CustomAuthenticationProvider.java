/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.security;

import com.mcc40.crud.entities.MyUserDetails;
import com.mcc40.crud.services.MyUserDetailsService;
import com.mcc40.crud.services.UserService;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author Yoshua
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    MyUserDetailsService myUserDetailsService;
    UserService userService;
    PasswordEncoder encoder;

    @Autowired
    public CustomAuthenticationProvider(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {
        String username = auth.getName();
        String password = auth.getCredentials().toString();
        MyUserDetails user = (MyUserDetails) myUserDetailsService.loadUserByUsername(username);

        if (user.getStatusCode() == -1) {
            throw new LockedException("User Not Verified");
        }

        if (user.getStatusCode() == 3) {
            throw new LockedException("User Banned");
        }
        
        if (encoder.matches(password, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), Collections.emptyList());
        } else {
            userService.changeStatusWrongCredential(user.getUsername());
            throw new BadCredentialsException("External system authentication failed");
        }
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
