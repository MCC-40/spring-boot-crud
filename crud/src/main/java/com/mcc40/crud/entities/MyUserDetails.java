/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.entities;

import java.util.Collection;
import java.sql.Date;
import java.time.LocalDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Mochamad Yusuf
 */
public class MyUserDetails implements UserDetails {

    private Collection<Role> roles;
    private String userName;
    private String password;
    private Integer status;
    private Date initDate;

    public MyUserDetails(User user) {
        roles = user.getRoleList();
        userName = user.getUserName();
        password = user.getPassword();
        status = user.getStatus().getId();
        initDate = Date.valueOf(LocalDate.now());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != 3;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return status != -1;
    }

}
