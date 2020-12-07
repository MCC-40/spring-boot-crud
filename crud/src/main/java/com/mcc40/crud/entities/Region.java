/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import lombok.Data;

/**
 *
 * @author aqira
 */
@Entity
@Table(name = "regions")
@XmlRootElement
@Data
public class Region implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regionId")
    private Collection<Country> countriesCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "region_id")
    private Integer regionId;
    @Column(name = "region_name")
    private String regionName;

    public Region() {
    }

    @XmlTransient
    public Collection<Country> getCountriesCollection() {
        return countriesCollection;
    }

    public void setCountriesCollection(Collection<Country> countriesCollection) {
        this.countriesCollection = countriesCollection;
    }
}
