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

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "WASHING_BATCH_DETAIL")
public class WashingBatchDetail implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(name = "washing_batch_id", nullable = false)
    private Long washingBatchId;

    @Column(name = "tracking_id", nullable = false)
    private Long trackingId;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "creator", nullable = false)
    private Long creator;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "washing_batch_id",insertable = false,updatable = false)
    @JsonBackReference
    private WashingBatch washingBatch;
}
