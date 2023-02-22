package com.surgical.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@SuppressWarnings("serial")
@Entity
@Data
@Table(name = "TRACKING")
public class Tracking implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "finish_time")
    private Date finishTime;
    
    @Column(name = "expire_time")
    private Date expireTime;
    
    @Column(name = "use_time")
    private Date useTime;
}
