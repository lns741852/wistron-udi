package com.surgical.entities;

import java.io.Serializable;
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
@Entity
@Data
@Table(name = "DEVICE_MODEL")
public class DeviceModel implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "manufacture_id", nullable = false)
    private String manufactureId;
    
    @OneToMany(mappedBy = "deviceModel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Device> devices;
    
    public DeviceModel(){
        super();
    }

    public DeviceModel(String brand, String manufactureId){
        super();
        this.brand = brand;
        this.manufactureId = manufactureId;
    }
}
