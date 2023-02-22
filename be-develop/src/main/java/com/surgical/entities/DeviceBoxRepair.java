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
@Table(name = "DEVICE_BOX_REPAIR")
public class DeviceBoxRepair implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_box_id", nullable = false)
    private Long deviceBoxId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "previous_used_count")
    private Long previousUsedCount;

    @Column(name = "comments")
    private String comments;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "finish_time")
    private Date finishTime;

    @Column(name = "updater", nullable = false)
    private Long updater;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getDeviceBoxId(){
        return deviceBoxId;
    }

    public void setDeviceBoxId(Long deviceBoxId){
        this.deviceBoxId = deviceBoxId;
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

    public String getComments(){
        return comments;
    }

    public void setComments(String comments){
        this.comments = comments;
    }

    public Date getCreateTime(){
        return createTime;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getFinishTime(){
        return finishTime;
    }

    public void setFinishTime(Date finishTime){
        this.finishTime = finishTime;
    }

    public Long getUpdater(){
        return updater;
    }

    public void setUpdater(Long updater){
        this.updater = updater;
    }
}
