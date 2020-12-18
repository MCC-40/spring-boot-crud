/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.entities;

import lombok.Data;

/**
 *
 * @author asus
 */
@Data
public class ChangePasswordData {
    String id, 
            newPassword, 
            oldPassword;
}
