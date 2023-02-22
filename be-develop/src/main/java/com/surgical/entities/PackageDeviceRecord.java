package com.surgical.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@SuppressWarnings("serial")
@Entity
@Table(name = "PACKAGE_DEVICE_RECORD")
@Data
public class PackageDeviceRecord implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "device_in_time", nullable = false)
    private Date deviceInTime;

    @Column(name = "device_out_time")
    private Date deviceOutTime;

    @Column(name = "creator", nullable = false)
    private Long creator;

    @Column(name = "updater", nullable = false)
    private Long updater;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    private Device device;
}
