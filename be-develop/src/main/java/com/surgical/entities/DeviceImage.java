package com.surgical.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Entity
@Table(name = "DEVICE_IMAGE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(DeviceImageId.class)
public class DeviceImage implements Serializable{

    @Id
    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @Id
    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "is_main", nullable = false)
    private Boolean isMain;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    @JsonBackReference
    private DeviceType deviceType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    private FileResource fileResource;
}
