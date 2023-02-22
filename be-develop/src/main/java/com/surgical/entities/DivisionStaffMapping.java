package com.surgical.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "DIVISION_STAFF_MAPPING")
public class DivisionStaffMapping implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", nullable = false)
    private String adminId;

    @Column(name = "division_id")
    private Integer divisionId;

    @Column(name = "branch_id")
    private Integer branchId;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    @Column(name = "updater")
    private String updater;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = Division.class)
    @JoinColumn(name = "division_id", insertable = false, updatable = false)
    private Division division;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = Branch.class)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    private Branch branch;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getAdminId(){
        return adminId;
    }

    public void setAdminId(String adminId){
        this.adminId = adminId;
    }

    public Integer getDivisionId(){
        return divisionId;
    }

    public void setDivisionId(Integer divisionId){
        this.divisionId = divisionId;
    }

    public Integer getBranchId(){
        return branchId;
    }

    public void setBranchId(Integer branchId){
        this.branchId = branchId;
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

    public Division getDivision(){
        return division;
    }

    public void setDivision(Division division){
        this.division = division;
    }

    public Branch getBranch(){
        return branch;
    }

    public void setBranch(Branch branch){
        this.branch = branch;
    }
}
