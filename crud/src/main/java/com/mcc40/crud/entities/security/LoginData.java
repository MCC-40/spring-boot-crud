/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.entities.security;

import lombok.Data;

/**
 *
 * @author Mochamad Yusuf
 */
@Data
public class LoginData {

    private String username,
            password;
}
