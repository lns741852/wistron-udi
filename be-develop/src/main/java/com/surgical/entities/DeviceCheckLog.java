package com.surgical.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@Entity
@Table(name="DEVICE_CHECK_LOG")
public class DeviceCheckLog{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "device_id", nullable = false)
    private Long deviceId;
    
    @Column(name = "status", nullable = false)
    private Integer status;
    
    @Column(name = "tracking_id", nullable = false)
    private Long trackingId;
    
    @Column(name = "type", nullable = false)
    private Integer type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", nullable = false)
    private Date createTime;
    
    @Column(name = "creator", nullable = false)
    private Long creator;
    
    @Column(name = "return_time", nullable = true)
    private Date returnTime;
}
