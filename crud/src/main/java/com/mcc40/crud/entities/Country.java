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
@Table(name = "countries")
@XmlRootElement
@Data
public class Country implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "country_id")
    private String countryId;
    @Column(name = "country_name")
    private String countryName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "countryId")
    private Collection<Location> locationsCollection;
    @JoinColumn(name = "region_id", referencedColumnName = "region_id")
    @ManyToOne(optional = false)
    private Region regionId;

    public Country() {
    }



    @XmlTransient
    public Collection<Location> getLocationsCollection() {
        return locationsCollection;
    }

    public void setLocationsCollection(Collection<Location> locationsCollection) {
        this.locationsCollection = locationsCollection;
    }

}
