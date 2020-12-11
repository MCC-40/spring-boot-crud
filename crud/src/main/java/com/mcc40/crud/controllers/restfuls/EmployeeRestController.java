/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.Employee;
import com.mcc40.crud.services.EmployeeService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Yoshua
 */
@RestController
@RequestMapping("api/employee")
public class EmployeeRestController {

    EmployeeService service;

    @Autowired
    public EmployeeRestController(EmployeeService service) {
        this.service = service;
    }

    public static Map<String, Object> MapTheEmployee(Employee employee) {
        Map<String, Object> e = new HashMap<>();
        e.put("id", employee.getId());
        e.put("firstName", employee.getFirstName());
        e.put("lastName", employee.getLastName());
        e.put("email", employee.getEmail());
        e.put("phoneNumber", employee.getPhoneNumber());
        e.put("hireDate", employee.getHireDate());
        e.put("job", employee.getJob().getId());
        e.put("salary", employee.getSalary());
        e.put("commissionPct", employee.getCommissionPct());
        e.put("manager", employee.getManager() == null ? null : employee.getManager().getId());
        e.put("department", employee.getDepartment() == null ? null : employee.getDepartment().getId());
        return e;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getById(int id) {
        Employee employee = service.getByIdEmployee(id);
        return ResponseEntity.ok().body(MapTheEmployee(employee));
    }

    @GetMapping("search")
    public ResponseEntity<List<Map<String, Object>>> searchJob(String keyword) {
        System.out.println(keyword);
        List<Employee> employees = service.getAllEmployee();
        List<Employee> result = employees.stream()
                .filter(employee
                        -> Integer.toString(employee.getId()).contains(keyword)
                || employee.getLastName().toLowerCase().contains(keyword.toLowerCase())
                || employee.getFirstName().toLowerCase().contains(keyword.toLowerCase())
                )
                .collect(Collectors.toList());

        List<Map<String, Object>> mapEmployeeList = new ArrayList<>();
        for (Employee employee : result) {
            mapEmployeeList.add(MapTheEmployee(employee));
        }
        return ResponseEntity.ok().body(mapEmployeeList);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> insertEmployee(@Validated @RequestBody Employee employee) {
        Map status = new HashMap();
        if (employee.getId() == null) {
            status.put("Status", "No Content");
            return ResponseEntity.status(200).body(status);
        } else if (service.isJobPresent(employee.getId())) {
            status.put("Status", "Use Method PUT to update");
            return ResponseEntity.status(200).body(status);
        }
        String result = service.saveEmployee(employee);
        status.put("Status", result);
        if (result.equals("Inserted")) {
            return ResponseEntity.accepted().body(status);
        }
        return ResponseEntity.status(500).body(status);
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateLastName(@Validated @RequestBody Employee employee) {
        Map status = new HashMap();
        if (employee.getId() == null) {
            status.put("Status", "No Content");
            return ResponseEntity.status(200).body(status);
        } else if (!service.isJobPresent(employee.getId())) {
            status.put("Status", "Use Method POST to insert new data");
            return ResponseEntity.status(200).body(status);
        }
        String result = service.saveEmployee(employee);
        status.put("Status", result);
        if (result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        }
        return ResponseEntity.status(500).body(status);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteEmployee(int id) {
        if (service.deleteEmployee(id)) {
            return ResponseEntity.status(200).body("Delete Success");
        }
        return ResponseEntity.status(500).body("Delete Fail");
    }
}
