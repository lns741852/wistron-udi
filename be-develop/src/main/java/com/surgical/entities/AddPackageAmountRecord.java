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

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@SuppressWarnings("serial")
@Entity
@Data
@Table(name = "ADD_PACKAGE_AMOUNT_RECORD")
public class AddPackageAmountRecord implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Column(name = "station_id", nullable = false)
//    private Long stationId;
    @Column(name = "package_config_id", nullable = false)
    private Long packageConfigId;
//    @Column(name = "amount", nullable = false)
//    private Integer amount;
//    @Column(name = "packaged_amount", nullable = false)
//    private Integer packagedAmount;
    @Column(name = "create_time", nullable = false)
    private Date createTime;
    @Column(name = "creator", nullable = false)
    private Long creator;
    @Column(name = "update_time", nullable = false)
    private Date updateTime;
    @Column(name = "updater", nullable = false)
    private Long updater;
    @Column(name = "status", nullable = false)
    private Integer status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_config_id", insertable = false, updatable = false)
    private PackageConfig packageConfig;
    
    @OneToMany(mappedBy = "addPackageAmountRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Package> packages;
}
