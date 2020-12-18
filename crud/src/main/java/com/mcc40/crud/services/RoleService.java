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
 * @author asus
 */
@Service
public class RoleService {
    
    @Autowired
    RoleRepository roleRepository;
    
    //get All
    public List<Role> getAllRoles(){
        return roleRepository.findAll();
    }
    
    //get by id
    public Role getRoleById(int id){
        return roleRepository.findById(id).get();
    }
    
    public String saveRole(Role role){
        String result = null;
        Optional<Role> optionalRole = roleRepository.findById(role.getId());
        if(optionalRole.isPresent() == false){
            roleRepository.save(role);
            result = "Inserted";
        } else {
            Role oldRole = optionalRole.get();
            oldRole.setName(role.getName());
            roleRepository.save(oldRole);
            result = "Updated";
        }
        return result;
    }
    
    public boolean deleteById(int id){
        roleRepository.deleteById(id);
        return !roleRepository.findById(id).isPresent();
    }
}
