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

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "WASHING_BATCH")
public class WashingBatch implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "washing_machine", nullable = false)
    private String washingMachine;

    @Column(name = "status")
    private Integer status;

    @Column(name = "finish_time")
    private Date finishTime;

    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "creator", nullable = false)
    private Long creator;
    
    @OneToMany(mappedBy = "washingBatch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<WashingBatchDetail> washingBatchDetails;
}
