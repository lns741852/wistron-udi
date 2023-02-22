package com.surgical.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "PACKAGE_DEVICE_CHECK")
public class PackageDeviceCheck implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_id", nullable = false)
    private Long trackingId;

    @Column(name = "step", nullable = false)
    private Integer step;

    @Column(name = "creator", nullable = false)
    private Integer creator;

    @Column(name = "updater", nullable = false)
    private Integer updator;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    private Date updateTime;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_device_check_id")
    private List<DeviceCheckDiff> deviceCheckDiffs;
}
