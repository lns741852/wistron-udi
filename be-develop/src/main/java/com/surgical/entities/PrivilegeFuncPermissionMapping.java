package com.surgical.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "PRIVILEGE_FUNC_PERMISSION_MAPPING")
public class PrivilegeFuncPermissionMapping implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "privilege_id", nullable = false)
    private Long privilegeId;

    @Column(name = "func_permission_id", nullable = false)
    private Long funcPermissionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "privilege_id", insertable = false, updatable = false)
    @JsonBackReference
    private Privilege privilege;
}
