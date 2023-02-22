package com.surgical.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "PACKAGE_CONFIG_DETAIL")
public class PackageConfigDetail implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "package_config_id",nullable = false)
    private Long packageConfigId;
    @Column(name = "device_type_id", nullable = false)
    private Long deviceTypeId;
    @Column(name = "amount", nullable = false)
    private Integer amount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_config_id",insertable = false,updatable = false)
    @JsonBackReference
    private PackageConfig packageConfig;

}
