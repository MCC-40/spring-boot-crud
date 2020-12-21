/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcc40.crud.entities.Status;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author Mochamad Yusuf
 */
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager,
            JwtConfig jwtConfig,
            SecretKey secretKey,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsernamePasswordAuthenticationToken authenticate(String username, String password)
            throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);

        if (optionalUser.isPresent()) {                                 // User exist
            User user = optionalUser.get();
            Integer userStatus = user.getStatus().getId();
            if (passwordEncoder.matches(password, user.getPassword())) {   // Comparing password
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

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        try {
            UsernameAndPasswordRequest authRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UsernameAndPasswordRequest.class);

            String userName = authRequest.getUsername();
            String password = authRequest.getPassword();
            
            Authentication authentication = authenticate(
                    userName,
                    password
            );
            
            System.out.println(authentication.getAuthorities());
            System.out.println(authentication.getCredentials());
            System.out.println(authentication.getName());
            Authentication authenticate = authenticationManager.authenticate(authentication);
            return authenticate;

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                .signWith(secretKey)
                .compact();
        response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + token);
    }

}
