/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.entities.data;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author asus
 */
@Data
public class RegisterData {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Date hireDate;
    private String job;
    private BigDecimal salary;
    private BigDecimal commissionPct;
    private Integer manager;
    private Integer department;
    private String username;
    private String password;
}
