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

@SuppressWarnings("serial")
@Entity
@Table(name = "FILE_RESOURCE")
public class FileResource implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "upload_ip", nullable = false)
    private String uploadIp;

    @Column(name = "resource_name", nullable = false)
    private String resourceName;

    @Column(name = "local_path", nullable = false)
    private String localPath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "status")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    @Column(name = "updater", nullable = false)
    private String updater;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getUploadIp(){
        return uploadIp;
    }

    public void setUploadIp(String uploadIp){
        this.uploadIp = uploadIp;
    }

    public String getResourceName(){
        return resourceName;
    }

    public void setResourceName(String resourceName){
        this.resourceName = resourceName;
    }

    public String getLocalPath(){
        return localPath;
    }

    public void setLocalPath(String localPath){
        this.localPath = localPath;
    }

    public Long getFileSize(){
        return fileSize;
    }

    public void setFileSize(Long fileSize){
        this.fileSize = fileSize;
    }

    public String getContentType(){
        return contentType;
    }

    public void setContentType(String contentType){
        this.contentType = contentType;
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

    public String getUpdater(){
        return updater;
    }

    public void setUpdater(String updater){
        this.updater = updater;
    }
}
