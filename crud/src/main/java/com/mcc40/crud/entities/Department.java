/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import lombok.Data;

/**
 *
 * @author Yoshua
 */
@Entity
@Table(name = "departments")
@XmlRootElement
@Data
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "department_id")
    private Integer departmentId;
    @Basic(optional = false)
    @Column(name = "department_name")
    private String departmentName;
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    @ManyToOne
    private Location locationId;
    @JoinColumn(name = "manager_id", referencedColumnName = "employee_id")
    @ManyToOne
    private Employee managerId;
    @OneToMany(mappedBy = "departmentId")
    private Collection<Employee> employeesCollection;

    public Department() {
    }

    @XmlTransient
    public Collection<Employee> getEmployeesCollection() {
        return employeesCollection;
    }

    public void setEmployeesCollection(Collection<Employee> employeesCollection) {
        this.employeesCollection = employeesCollection;
    }
}
