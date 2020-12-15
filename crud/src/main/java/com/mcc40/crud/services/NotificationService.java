/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.EmployeeRepository;
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
    EmployeeRepository employeeRepository;

    @Autowired
    public NotificationService(JavaMailSender javaMailSender, EmployeeRepository employeeRepository) {
        this.javaMailSender = javaMailSender;
        this.employeeRepository = employeeRepository;
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

    public boolean sendVerificationMail(int id, String verificattionCode) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(email);
            mailMessage.setTo(employee.getEmail());
            mailMessage.setSubject("Verification Mail");
            mailMessage.setText("http://localhost:8081/api/user/verify?token=" + verificattionCode);
            try {
                javaMailSender.send(mailMessage);
            } catch (MailException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;

    }

}
