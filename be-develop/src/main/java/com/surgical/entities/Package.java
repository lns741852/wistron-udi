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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@SuppressWarnings("serial")
@Entity
@Table(name = "PACKAGE")
@Data
public class Package implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "add_id", nullable = false)
    private Long addId;

    @Column(name = "package_config_id", nullable = false)
    private Long packageConfigId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "device_box_qrcode", unique = true)
    private String deviceBoxQrcode;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "tracking_id")
    private Long trackingId;

    @Column(name = "serial_no", nullable = false)
    private String serialNo;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "position")
    private String position;

    @Column(name = "used_count")
    private Long usedCount = 0L;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_config_id", insertable = false, updatable = false)
    @JsonBackReference
    private PackageConfig packageConfig;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_id", insertable = false, updatable = false)
    private Tracking tracking;

    @OneToMany(mappedBy = "pack", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PackageOrderMapping> packageOrderMappings;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_id", referencedColumnName = "tracking_id", insertable = false, updatable = false)
    private PackageOrderMapping packageOrderMapping;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "add_id", insertable = false, updatable = false)
    @JsonBackReference
    private AddPackageAmountRecord addPackageAmountRecord;
    
    @OneToMany(mappedBy = "pack", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Device> device;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_box_qrcode", referencedColumnName = "qrcode", insertable = false, updatable = false)
    private DeviceBox deviceBox;
}