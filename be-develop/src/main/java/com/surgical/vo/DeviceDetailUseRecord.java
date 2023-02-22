package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class DeviceDetailUseRecord{

    private Long surgicalApplyId;

    private String medicalRecordNumber;

    private String surgicalName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date operatingDate;

    private String operatingNumber;

    private Long divisionId;

    public DeviceDetailUseRecord(Long surgicalApplyId, String medicalRecordNumber, String surgicalName, Date operatingDate, String operatingNumber, Long divisionId){
        super();
        this.surgicalApplyId = surgicalApplyId;
        this.medicalRecordNumber = medicalRecordNumber;
        this.surgicalName = surgicalName;
        this.operatingDate = operatingDate;
        this.operatingNumber = operatingNumber;
        this.divisionId = divisionId;
    }
}
