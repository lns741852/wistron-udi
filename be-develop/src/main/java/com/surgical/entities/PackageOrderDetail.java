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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@SuppressWarnings("serial")
@Entity
@Data
@Table(name = "PACKAGE_ORDER_DETAIL")
public class PackageOrderDetail implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_order_id", nullable = false)
    private Long packageOrderId;

    @Column(name = "package_config_id", nullable = false)
    private Long packageConfigId;

    @Column(name = "amount", nullable = false)
    private Integer amount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_order_id", insertable = false, updatable = false)
    @JsonBackReference
    private PackageOrder packageOrder;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_config_id", insertable = false, updatable = false)
    @JsonManagedReference
    private PackageConfig packageConfig;
}
