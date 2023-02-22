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
@Data
@Entity
@Table(name = "STATION")
public class Station implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "station_name", nullable = false)
    private String stationName;
    @Column(name = "is_active", nullable = false)
    private Integer isActive;
    @Column(name = "type", nullable = false)
    private String type;
    
}
