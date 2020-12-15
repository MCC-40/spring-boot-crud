/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.User;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    public void javaSimpleEmail(User user, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        System.out.println("From: " + sender);
        mailMessage.setFrom(sender);
        mailMessage.setTo(user.getEmployee().getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        javaMailSender.send(mailMessage);
    }

    public void javaMimeEmail(User user, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        System.out.println("From: " + sender);
        helper.setFrom(sender);
        helper.setTo(user.getEmployee().getEmail());
        helper.setSubject(subject);
        message.setText(body, "UTF-8", "html");
        
        javaMailSender.send(message);
    }
}
