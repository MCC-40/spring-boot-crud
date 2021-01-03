/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.services.EmployeeService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@RequestMapping("api/employees")
public class EmployeeRestController {

    EmployeeService service;

    @Autowired
    public EmployeeRestController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getByKeyword(String keyword) {
        System.out.println("search: " + keyword);
        List<Employee> mapList = service.getByKeyword(keyword);
        if (mapList.size() > 0) {
            System.out.println("return " + mapList.size());
            return ResponseEntity.status(200).body(mapList);
        } else {
            return ResponseEntity.status(404).build();
        }
    }
    
    @GetMapping("id")
    public ResponseEntity<Employee> getById(Integer id) {
        System.out.println(id);
        Employee map = service.getById(id);
        if (map != null) {
            return ResponseEntity.status(200).body(map);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> insert(@RequestBody Employee employee) {
        System.out.println(employee);
        Map status = new HashMap();
        
        String result = service.insert(employee);
        status.put("Status", result);
        if (result.equals("Inserted")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> update(@RequestBody Employee employee) {
        System.out.println(employee);
        Map status = new HashMap();

        String result = service.update(employee);
        status.put("Status", result);
        
        if (result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> delete(int id) {
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
