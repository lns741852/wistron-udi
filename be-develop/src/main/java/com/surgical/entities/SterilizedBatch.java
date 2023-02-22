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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "STERILIZED_BATCH")
public class SterilizedBatch implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sterilizer", nullable = false)
    private String sterilizer;
    
//    @Column(name = "station_id", nullable = false)
//    private Long stationId;

    @Column(name = "status")
    private Integer status;
    
    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "finish_time")
    private Date finishTime;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "creator", nullable = false)
    private Long creator;
    
    @Column(name = "petri_dish", nullable = false)
    private String petriDish;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sterilized_batch_id")
    private List<SterilizedBatchDetail> sterilizedBatchDetails;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sterilized_batch_id")
    private List<SterilizedMonitor> sterilizedMonitors;
}
