/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.config;

import com.mcc40.crud.security.CustomAuthenticationProvider;
import com.mcc40.crud.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@ComponentScan(basePackages = {"com.mcc40.crud"})
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthenticationProvider authProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/users/forgot-password/**").permitAll()
                .antMatchers("/api/users/reset-password/**").permitAll()
                .antMatchers("/api/users/verify/**").permitAll()
                .antMatchers("/api/users/register/**").permitAll()
                .antMatchers("/api/users/login/**").permitAll()
                .antMatchers("/api/departments/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/location/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/jobs/**").hasAnyRole("HR", "ADMIN")
                .antMatchers("api/employee/**").hasAnyRole("HR", "ADMIN")
                .antMatchers("/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and().formLogin();
        http.httpBasic();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
