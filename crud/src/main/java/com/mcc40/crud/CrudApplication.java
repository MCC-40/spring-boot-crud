package com.mcc40.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudApplication.class, args);
                System.out.println("Sudah berjalan, ga usah nungguin lagi!!!");
	}
//        
//        @Bean //IOC
//        public JavaMailSender getJavaMailSender(){
//            return new JavaMailSenderImpl();
//        }

}
