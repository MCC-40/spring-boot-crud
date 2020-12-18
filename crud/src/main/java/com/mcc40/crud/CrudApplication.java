package com.mcc40.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootApplication
public class CrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudApplication.class, args);
                System.out.println("Server sudah berjalan, ga usah nungguin lagi!!!");
	}
        
//        @Bean
//        public JavaMailSender getJavaMailSender(){
//            return new JavaMailSenderImpl(); 
//        }
}
