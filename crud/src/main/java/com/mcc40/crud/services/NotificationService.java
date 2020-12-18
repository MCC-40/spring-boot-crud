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
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 *
 * @author asus
 */
@Service
public class NotificationService {
    JavaMailSender javaMailSender;
    
    @Autowired
    public NotificationService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }
    
    @Value("${spring.mail.username}")
    private String email;
    
    public boolean javaSimpleEmail(String emailTo){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(email);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject("Email Verification");
        mailMessage.setText("Verfication here");
        try {
            javaMailSender.send(mailMessage);        
        } catch (MailException e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    public boolean javaMimeMessage(User user) throws MessagingException{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(this.email);
        helper.setTo(user.getEmployee().getEmail());
        helper.setSubject("Email Verification Account");
        String body =   "<html>\n" +
                        "<body>\n" +
                        "Verification account in \n" +
                        "<a href='http://Localhost:8081/api/users/verify/"+user.getVerificationCode()+"/'>here</a>\n" +
                        "</body>\n" +
                        "</html>";
        message.setText(body, "UTF-8", "html");
        javaMailSender.send(message);
        
        return true;
    }
    
     public boolean javaMimeMessageForgotPassword(User user) throws MessagingException{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(this.email);
        helper.setTo(user.getEmployee().getEmail());
        helper.setSubject("Email Verification Forgot Password");
        String body =   "<html>\n" +
                        "<body>\n" +
                        "Verification forgot password in\n" +
                        "<a href='http://Localhost:8082/users/form-forgot-password/"+user.getVerificationCode()+"/'>here</a>\n" +
                        "</body>\n" +
                        "</html>";
        message.setText(body, "UTF-8", "html");
        javaMailSender.send(message);
        
        return true;
    }
}
