package com.surgical.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@SuppressWarnings("serial")
@Entity
@Data
@Table(name = "DELIVER_BATCH_DETAIL")
public class DeliverBatchDetail implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(name = "tracking_id", nullable = false)
    private Long trackingId;

    @Column(name = "deliver_batch_id", nullable = false)
    private Long deliverBatchId;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "receive_time")
    private Date receiveTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    private Package pkg;
}
