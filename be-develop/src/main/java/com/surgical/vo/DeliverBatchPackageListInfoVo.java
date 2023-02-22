package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class DeliverBatchPackageListInfoVo{

    private Long id;

    private String qrcode;
    
    private String packageCode;
    
    private String serialNo;
    
    private String position;

    private Long trackingId;

    private Long configId;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String configCode;

    private String configName;

    private Long divisionId;

    private Integer status;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date receiveTime;
}
