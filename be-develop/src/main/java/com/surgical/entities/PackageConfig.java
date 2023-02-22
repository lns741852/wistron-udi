package com.surgical.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "PACKAGE_CONFIG")
public class PackageConfig implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="config_code", nullable = false)
    private String configCode;
    @Column(name = "package_name", nullable = false)
    private String packageName;
    @Column(name = "division_id", nullable = false)
    private Long divisionId;
    @Column(name = "create_time", nullable = false)
    private Date createTime;
    @Column(name = "update_time", nullable = false)
    private Date updateTime;
    @Column(name = "creator", nullable = false)
    private Long creator;
    @Column(name = "updater", nullable = false)
    private Long updater;
    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1;
    
    @OneToMany(mappedBy = "packageConfig", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PackageConfigDetail> packageConfigDetails;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id", insertable = false, updatable = false)
    @JsonBackReference
    private Division division;
    
    @OneToMany(mappedBy = "packageConfig", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Package> packages;
}
