/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yoshua
 */
@Service
public class NotificationService {

    JavaMailSender javaMailSender;
    UserRepository userRepository;

    @Autowired
    public NotificationService(JavaMailSender javaMailSender, UserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
    }

    @Value("${spring.mail.username}")
    private String email;

    public boolean javaSimpleEmail(String emailTo) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(email);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject("Mail Belajar java mail");
        mailMessage.setText("Ini adalah isi body");
        try {
            javaMailSender.send(mailMessage);
        } catch (MailException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean sendVerificationMail(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(email);
            mailMessage.setTo(user.getEmployee().getEmail());
            mailMessage.setSubject("Verification Mail");
            mailMessage.setText("http://localhost:8081/api/user/verify?token=" + user.getVerificationCode());
            return true;
        }
        return false;

    }

}
