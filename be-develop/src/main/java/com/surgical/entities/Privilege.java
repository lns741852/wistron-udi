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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@SuppressWarnings("serial")
@Entity
@Table(name = "PRIVILEGE")
public class Privilege implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    private Integer status;
    
    @Column(name = "privilege_level", nullable = false)
    private Integer privilegeLevel;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    @Column(name = "updater", nullable = false)
    private Integer updater;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "privilege")
    private List<Admin> admin;

    @OneToMany(mappedBy = "privilege", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PrivilegeFuncPermissionMapping> privilegeFuncPermissionMappings;
    
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

    public Integer getStatus(){
        return status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }
    
    public Integer getPrivilegeLevel(){
        return privilegeLevel;
    }

    public void setPrivilegeLevel(Integer privilegeLevel){
        this.privilegeLevel = privilegeLevel;
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

    public Integer getUpdater(){
        return updater;
    }

    public void setUpdater(Integer updater){
        this.updater = updater;
    }

    public List<Admin> getAdmin(){
        return admin;
    }

    public void setAdmin(List<Admin> admin){
        this.admin = admin;
    }

    public List<PrivilegeFuncPermissionMapping> getPrivilegeFuncPermissionMappings(){
        return privilegeFuncPermissionMappings;
    }

    public void setPrivilegeFuncPermissionMappings(List<PrivilegeFuncPermissionMapping> privilegeFuncPermissionMappings){
        this.privilegeFuncPermissionMappings = privilegeFuncPermissionMappings;
    }
}
