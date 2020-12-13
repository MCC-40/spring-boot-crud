/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.services;

import com.mcc40.crud.entities.Role;
import com.mcc40.crud.entities.User;
import com.mcc40.crud.repositories.RoleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mochamad Yusuf
 */
@Service
public class RoleService {

    RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    //get all 
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    //get by id
    public Role getByIdRole(int id) {
        Optional<Role> role = roleRepository.findById(id);
        if (!role.isPresent()) {
            return null;
        } else {
            return role.get();
        }
    }

    //insert
    public String insertRole(Role role) {
        String result = "Unknown Error";
        Optional<Role> optionalRole = roleRepository.findById(role.getId());
        try {
            if (optionalRole.isPresent() == false) {
                roleRepository.save(role);
                result = "Inserted";
            } else {
//                Role oldRole = optionalRole.get();
//                oldRole.setName(role.getName());
//                role = oldRole;
//                result = "Updated";
                result = "Id already exist";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        roleRepository.save(role);
        return result;
    }

    public String putRole(Role role) {
        String result = "Unknown Error";
        Optional<Role> optionalRole = roleRepository.findById(role.getId());
        try {
            if (optionalRole.isPresent() == false) {
//                roleRepository.save(role);
//                result = "Inserted";
                result = "Id is not exist";
            } else {
                Role oldRole = optionalRole.get();
                if (role.getName() != null) {
                    oldRole.setName(role.getName());
                }
                role = oldRole;
                result = "Updated";
            }
        } catch (Exception e) {
            result = "Unknown Error";
            System.out.println(e.toString());
        }
        roleRepository.save(role);
        return result;
    }

    //delete
    public boolean deleteRole(int id) {
        roleRepository.deleteById(id);
        return !roleRepository.findById(id).isPresent();
    }

}
