/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.jwt;

import com.google.common.base.Strings;
import com.mcc40.crud.entities.MyUserDetails;
import com.mcc40.crud.services.MyUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author Mochamad Yusuf
 */
@Component
public class JwtTokenVerifier extends OncePerRequestFilter {

    private final JwtSecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final MyUserDetailsService service;
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtTokenVerifier(JwtSecretKey secretKey, JwtConfig jwtConfig, MyUserDetailsService service, JwtUtil jwtUtil) {
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest hsr,
            HttpServletResponse hsr1,
            FilterChain fc) throws ServletException, IOException {

        String authorizatonHeader = hsr.getHeader(jwtConfig.getAuthorizationHeader());

        if (Strings.isNullOrEmpty(authorizatonHeader) || !authorizatonHeader.startsWith(jwtConfig.getTokenPrefix())) {
            fc.doFilter(hsr, hsr1);
            return;
        }

        String token = authorizatonHeader.replace(jwtConfig.getTokenPrefix(), "");
        try {

            Jws<Claims> parseClaimsJws = Jwts.parser()
                    .setSigningKey(secretKey.getSecretKey())
                    .parseClaimsJws(token);
            Claims body = parseClaimsJws.getBody();

            String username = body.getSubject();

            MyUserDetails userDetails = service.loadUserByUsername(username);
            System.out.println(userDetails.getUsername());
            if (jwtUtil.validateToken(token, userDetails)) {

                Authentication aunthetication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(aunthetication);
            }

        } catch (JwtException je) {
            throw new IllegalStateException(String.format("Token %s cannot be trust", token));
        }

        fc.doFilter(hsr, hsr1);
    }

}
