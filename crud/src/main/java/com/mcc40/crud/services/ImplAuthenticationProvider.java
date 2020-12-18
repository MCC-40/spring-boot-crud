/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 *
 * @author asus
 */
@Component
public class ImplAuthenticationProvider implements AuthenticationProvider{

    @Autowired
    UserService userService;
    
    public ImplAuthenticationProvider(UserService userService){
        this.userService = userService;
    }
    
    @Override
    public Authentication authenticate(Authentication a) throws AuthenticationException {
        
        String name = a.getName();
        String password = a.getCredentials().toString();
        
        return userService.loginAuthentication(name, password);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
}
