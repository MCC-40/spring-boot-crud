/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.entities;

import lombok.Data;

/**
 *
 * @author Mochamad Yusuf
 */
@Data
public class ChangePasswordClass {
    public User user;
    public String verificationCode;
    public String oldPassword;
    public String newPassword;
}
