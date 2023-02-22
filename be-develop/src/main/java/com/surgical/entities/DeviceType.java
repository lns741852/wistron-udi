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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@SuppressWarnings("serial")
@Entity
@Data
@Table(name = "DEVICE_TYPE")
public class DeviceType implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "spec", nullable = true)
    private String spec;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "name_scientific", nullable = false)
    private String nameScientific;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    @Column(name = "updater", nullable = false)
    private Long updater;

    @OneToMany(mappedBy = "deviceType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DeviceImage> deviceImages;

    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private List<Device> devices;
    
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getSpec(){
        return spec;
    }

    public void setSpec(String spec){
        this.spec = spec;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getNameScientific(){
        return nameScientific;
    }

    public void setNameScientific(String nameScientific){
        this.nameScientific = nameScientific;
    }

    public Date getCreateTime(){
        return createTime;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getUpdateTime(){
        return updateTime;
    }

    public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
    }

    public Long getUpdater(){
        return updater;
    }

    public void setUpdater(Long updater){
        this.updater = updater;
    }

    public List<DeviceImage> getDeviceImages(){
        return deviceImages;
    }

    public void setDeviceImages(List<DeviceImage> deviceImages){
        this.deviceImages = deviceImages;
    }
    public List<Device> getDevices(){
        return devices;
    }

    public void setDevices(List<Device> devices){
        this.devices = devices;    
    }
}
