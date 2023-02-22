package com.surgical.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@SuppressWarnings("serial")
@Entity
@Data
@Table(name = "SURGICAL_APPLY")
public class SurgicalApply implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operating_number", nullable = false)
    private String operatingNumber;

    @Column(name = "division_id", nullable = false)
    private Long divisionId;

    @Column(name = "operating_room", nullable = false)
    private String operatingRoom;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "medical_record_number", nullable = false)
    private String medicalRecordNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "doctor", nullable = false)
    private String doctor;

    @Column(name = "operating_date", nullable = false)
    private Date operatingDate;

    @Column(name = "operating_order", nullable = false)
    private Integer operatingOrder;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    @Column(name = "updater", nullable = false)
    private Long updater;
    
    @Column(name = "creator", nullable = false)
    private Long creator;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "surgical_apply_id")
    private List<PackageOrder> orders ;
}
