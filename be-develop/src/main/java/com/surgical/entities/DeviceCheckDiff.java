package com.surgical.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "DEVICE_CHECK_DIFF")
public class DeviceCheckDiff implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_device_check_id", nullable = false)
    private Long packageDeviceCheckId;
    
    @Column(name = "device_type_id", nullable = false)
    private Long deviceTypeId;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "difference", nullable = false)
    private Integer difference;
}
