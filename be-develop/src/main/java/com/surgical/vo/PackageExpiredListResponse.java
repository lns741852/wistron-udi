package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PackageExpiredListResponse{

    private Long trackingId;

    private Long packageId;

    private String serialNo;

    private String configCode;

    private String configName;

    private Long divisionId;
    
    private String position;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date sterilizationDate;

    private String sterilizer;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;

    public PackageExpiredListResponse(Long trackingId, Long packageId, String serialNo, String configCode, String configName, String position, Long divisionId, Date sterilizationDate, String sterilizer, Date expireDate){
        super();
        this.trackingId = trackingId;
        this.packageId = packageId;
        this.serialNo = serialNo;
        this.configCode = configCode;
        this.configName = configName;
        this.position = position;
        this.divisionId = divisionId;
        this.sterilizationDate = sterilizationDate;
        this.sterilizer = sterilizer;
        this.expireDate = expireDate;
    }
}
