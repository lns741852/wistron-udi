package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ReportPackageHistoryListResponse{

    private Long trackingId;

    private Long packageId;

    private String serialNo;

    private String configCode;

    private String configName;

    private Long divisionId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date sterilizationDate;

    private String sterilizer;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;

    private String medicalRecordNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date usedDate;

    public ReportPackageHistoryListResponse(Long trackingId, Long packageId, String serialNo, String configCode, String configName, Long divisionId, Date sterilizationDate, String sterilizer, Date expireDate, String medicalRecordNo, Date usedDate){
        super();
        this.trackingId = trackingId;
        this.packageId = packageId;
        this.serialNo = serialNo;
        this.configCode = configCode;
        this.configName = configName;
        this.divisionId = divisionId;
        this.sterilizationDate = sterilizationDate;
        this.sterilizer = sterilizer;
        this.expireDate = expireDate;
        this.medicalRecordNo = medicalRecordNo;
        this.usedDate = usedDate;
    }
}
