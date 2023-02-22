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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

@SuppressWarnings("serial")
@Entity
@Table(name = "DEVICE_REPAIR_RECORD")
public class DeviceRepairRecord implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "previous_used_count", nullable = false)
    private Long previousUsedCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "updater", nullable = false)
    private Integer updater;

    @Column(name = "description", nullable = false)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "finish_time")
    private Date finishTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    @JsonBackReference
    private Device device;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getDeviceId(){
        return deviceId;
    }

    public void setDeviceId(Long deviceId){
        this.deviceId = deviceId;
    }

    public Integer getStatus(){
        return status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Long getPreviousUsedCount(){
        return previousUsedCount;
    }

    public void setPreviousUsedCount(Long previousUsedCount){
        this.previousUsedCount = previousUsedCount;
    }

    public Date getCreateTime(){
        return createTime;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Integer getUpdater(){
        return updater;
    }

    public void setUpdater(Integer updater){
        this.updater = updater;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public Date getFinishTime(){
        return finishTime;
    }

    public void setFinishTime(Date finishTime){
        this.finishTime = finishTime;
    }

    public Device getDevice(){
        return device;
    }

    public void setDevice(Device device){
        this.device = device;
    }
}
