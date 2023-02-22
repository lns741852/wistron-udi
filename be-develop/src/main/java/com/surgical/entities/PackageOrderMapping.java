package com.surgical.entities;

import java.io.Serializable;

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

import lombok.Data;

@SuppressWarnings("serial")
@Entity
@Data
@Table(name = "PACKAGE_ORDER_MAPPING")
public class PackageOrderMapping implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_order_id", nullable = false)
    private Long packageOrderId;

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(name = "tracking_id", nullable = false)
    private Long trackingId;
    
    @Column(name = "status", nullable = false)
    private Integer status=0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", insertable = false, updatable = false)
    @JsonBackReference
    private Package pack;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_order_id", insertable = false, updatable = false)
    private PackageOrder packageOrder;
}
