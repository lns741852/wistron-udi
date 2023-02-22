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
@Data
@Entity
@Table(name = "DELIVER_BATCH")
public class DeliverBatch implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "deliver_from", nullable = false)
    private Long deliverFrom;

    @Column(name = "deliver_to", nullable = false)
    private Long deliverTo;

    @Column(name = "create_time", nullable = false)
    private Date createTime;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliver_batch_id")
    private List<DeliverBatchDetail> deliverBatchDetails;
}
