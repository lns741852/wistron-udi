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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@SuppressWarnings("serial")
@Entity
@Data
@Table(name = "PACKAGE_ORDER")
public class PackageOrder implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "surgical_apply_id", nullable = false)
    private Long surgicalApplyId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "create_time", nullable = false)
    private Date createTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surgical_apply_id", insertable = false, updatable = false)
    private SurgicalApply surgicalApply;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_order_id")
    private List<PackageOrderDetail> packageOrderDetails;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_order_id", insertable = false, updatable = false)
    private List<PackageOrderMapping> packageOrderMappings;
}
