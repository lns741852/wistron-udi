package com.surgical.vo;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SurgicalApplyCreateRequest{

    @NotNull(message = "divisionId cannot be null")
    private Long divisionId;

    @NotEmpty(message = "operatingRoom cannot be empty")
    private String operatingRoom;

    @NotEmpty(message = "medicalRecordNumber cannot be empty")
    private String medicalRecordNumber;

    @NotEmpty(message = "surgeryName cannot be empty")
    private String surgeryName;

    @NotEmpty(message = "doctor cannot be empty")
    private String doctor;

    @NotNull(message = "operatingDate cannot be empty")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date operatingDate;

    @NotNull(message = "operatingOrder cannot be null")
    private Integer operatingOrder;
    
    @NotEmpty(message = "operatingNumber cannot be empty")
    private String operatingNumber;

    @Valid
    private List<SurgicalApplyCreateOrdersRequest> orders;
}
