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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@SuppressWarnings("serial")
@Entity
@Table(name = "DEVICE")
public class Device implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "scrap_time")
    private Date scrapTime;

    @Column(name = "updater", nullable = false)
    private Long updater;

    @Column(name = "udi")
    private String udi;

    @Column(name = "branch", nullable = false)
    private Integer branch;

    @Column(name = "division", nullable = false)
    private Integer division;

    @Column(name = "qrcode", nullable = false)
    private String qrcode;

    @Column(name = "cost")
    private Float cost;

    @Column(name = "used_count")
    private Long usedCount;

    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @Column(name = "used_time")
    private Date usedTime;

    @Column(name = "device_model_id", nullable = false)
    private Long deviceModelId;

    @Column(name = "package_id", nullable = true)
    private Long packageId;

    @Transient
    private Integer characteristic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private DeviceType deviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_model_id", insertable = false, updatable = false)
    @JsonBackReference
    private DeviceModel deviceModel;

    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DeviceRepairRecord> deviceRepairRecords;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", insertable = false, updatable = false)
    @JsonBackReference
    private Package pack;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Integer getStatus(){
        return status;
    }

    public void setStatus(Integer status){
        this.status = status;
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

    public Date getScrapTime(){
        return scrapTime;
    }

    public void setScrapTime(Date scrapTime){
        this.scrapTime = scrapTime;
    }

    public Long getUpdater(){
        return updater;
    }

    public void setUpdater(Long updater){
        this.updater = updater;
    }

    public String getUdi(){
        return udi;
    }

    public void setUdi(String udi){
        this.udi = udi;
    }

    public Integer getBranch(){
        return branch;
    }

    public void setBranch(Integer branch){
        this.branch = branch;
    }

    public Integer getDivision(){
        return division;
    }

    public void setDivision(Integer division){
        this.division = division;
    }

    public String getQrcode(){
        return qrcode;
    }

    public void setQrcode(String qrcode){
        this.qrcode = qrcode;
    }

    public Float getCost(){
        return cost;
    }

    public void setCost(Float cost){
        this.cost = cost;
    }

    public Long getUsedCount(){
        return usedCount;
    }

    public void setUsedCount(Long usedCount){
        this.usedCount = usedCount;
    }

    public Long getTypeId(){
        return typeId;
    }

    public void setTypeId(Long typeId){
        this.typeId = typeId;
    }

    public Date getUsedTime(){
        return usedTime;
    }

    public void setUsedTime(Date usedTime){
        this.usedTime = usedTime;
    }

    public Long getDeviceModelId(){
        return deviceModelId;
    }

    public void setDeviceModelId(Long deviceModelId){
        this.deviceModelId = deviceModelId;
    }

    public Long getPackageId(){
        return packageId;
    }

    public void setPackageId(Long packageId){
        this.packageId = packageId;
    }

    public Integer getCharacteristic(){
        return characteristic;
    }

    public void setCharacteristic(Integer characteristic){
        this.characteristic = characteristic;
    }

    public DeviceType getDeviceType(){
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType){
        this.deviceType = deviceType;
    }

    public DeviceModel getDeviceModel(){
        return deviceModel;
    }

    public void setDeviceModel(DeviceModel deviceModel){
        this.deviceModel = deviceModel;
    }

    public Package getPack(){
        return pack;
    }

    public void setPack(Package pack){
        this.pack = pack;
    }

    public List<DeviceRepairRecord> getDeviceRepairRecords(){
        return deviceRepairRecords;
    }

    public void setDeviceRepairRecords(List<DeviceRepairRecord> deviceRepairRecords){
        this.deviceRepairRecords = deviceRepairRecords;
    }
}
