/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mochamad Yusuf
 */
@Service
public class NotificationService {
    JavaMailSender javaMailSender;
    
    @Value("${spring.mail.username}")
    String sender;

    @Autowired
    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    
    public void javaSimpleEmail(String user){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        System.out.println("From: " + sender);
        mailMessage.setFrom(sender);
        mailMessage.setTo(user);
        mailMessage.setSubject("Mari belajar java mail");
        mailMessage.setText("Ini adalah body");
        javaMailSender.send(mailMessage);
    }
}
