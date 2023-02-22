package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SterilizedBatchPackageResponse{

    private Long trackingId;

    private Long packageId;
    
    private String serialNo;

    private String configCode;

    private String configName;

    private Long divisionId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date usedDate;

    public SterilizedBatchPackageResponse(Long trackingId, Long packageId, String serialNo, String configCode, String configName, Long divisionId, Date expireDate, Date usedDate){
        super();
        this.trackingId = trackingId;
        this.packageId = packageId;
        this.serialNo = serialNo;
        this.configCode = configCode;
        this.configName = configName;
        this.divisionId = divisionId;
        this.expireDate = expireDate;
        this.usedDate = usedDate;
    }

    
}
