/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.Department;
import com.mcc40.crud.services.DepartmentService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mochamad Yusuf
 */
@RestController
@RequestMapping("api/departments")
public class DepartmentRestController {

    DepartmentService service;

    @Autowired
    public DepartmentRestController(DepartmentService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<List<Map<String, Object>>> searchDepartment(String keyword, Integer id) {
        System.out.println(keyword);
        List<Map<String, Object>> mapList = service.searchByKeywordOrId(keyword, id);
        if (mapList.size() > 0) {
            return ResponseEntity.status(200).body(mapList);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> departmentSave(@RequestBody Department department) {
        System.out.println(department);
        System.out.println(department.getManager());
        Map status = new HashMap();
        
        String result = service.saveDepartment(department);
        status.put("Status", result);
        if (result.equals("Inserted") || result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    
    @PutMapping("")
    public ResponseEntity<Map<String, String>> departmentSaveWithPut(@RequestBody Department department) {
        System.out.println(department);
        Map status = new HashMap();

        String result = service.putDepartment(department);
        status.put("Status", result);
        if (result.equals("Inserted") || result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<Map<String, String>> deleteDepartmentById(int id) {
        Map status = new HashMap();
        if (service.deleteById(id)) {
            status.put("Status", "Success");
            return ResponseEntity.accepted().body(status);
        } else {
            status.put("Status", "Failed");
            return ResponseEntity.status(500).body(status);
        }
    }

}
