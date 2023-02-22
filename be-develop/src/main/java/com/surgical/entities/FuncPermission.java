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
@Data
@Entity
@Table(name = "FUNC_PERMISSION")
public class FuncPermission implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="name_cn", nullable = false)
    private String nameCN;
    
    @Column(name="name_en", nullable = false)
    private String nameEN;
    
    @Column(name="func_level", nullable = false)
    private Integer funcLevel;
    
    @Column(name="create_time", nullable = false)
    private Date createTime;
    
}
