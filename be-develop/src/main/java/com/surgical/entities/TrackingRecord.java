package com.surgical.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "TRACKING_RECORD")
public class TrackingRecord implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_id", nullable = false)
    private Long trackingId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "creator", nullable = false)
    private Long creator;

    public TrackingRecord(Long trackingId, Integer status, Date createTime, Long creator){
        super();
        this.trackingId = trackingId;
        this.status = status;
        this.createTime = createTime;
        this.creator = creator;
    }
    
    
}
