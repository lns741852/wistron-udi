package com.surgical.entities;

import java.io.Serializable;

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
@Table(name = "STERILIZED_MONITOR")
public class SterilizedMonitor implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sterilized_batch_id", nullable = false)
    private Long sterilizedBatchId;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "indicator")
    private String indicator;

    @Column(name = "result")
    private Integer result;
}
