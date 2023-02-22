package com.surgical.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "DEVICE_BOX")
public class DeviceBox implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qrcode", nullable = false)
    private String qrcode;

    @Column(name = "cost")
    private Float cost;

    @Column(name = "udi")
    private String udi;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "updater", nullable = false)
    private Long updater;

    @Column(name = "used_count")
    private Long usedCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "used_time")
    private Date usedTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "scrap_time")
    private Date scrapTime;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
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

    public String getUdi(){
        return udi;
    }

    public void setUdi(String udi){
        this.udi = udi;
    }

    public Integer getStatus(){
        return status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Long getUpdater(){
        return updater;
    }

    public void setUpdater(Long updater){
        this.updater = updater;
    }

    public Long getUsedCount(){
        return usedCount;
    }

    public void setUsedCount(Long usedCount){
        this.usedCount = usedCount;
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

    public Date getUsedTime(){
        return usedTime;
    }

    public void setUsedTime(Date usedTime){
        this.usedTime = usedTime;
    }

    public Date getScrapTime(){
        return scrapTime;
    }

    public void setScrapTime(Date scrapTime){
        this.scrapTime = scrapTime;
    }
}
