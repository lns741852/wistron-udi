package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data

public class PackageOrderInfoListVo{
    private Long applyId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long orderId;
    private String operatingNumber;
    private Long divisionId;
    private Integer status;       //PACKAGE_ORDER.status
    private String medicalRecordNumber;
    private String surgeryName;
    private String doctor;
    private String operatingRoom;
    private String operatingDate;
    private Integer operatingOrder;
    private Date createTime;
}
