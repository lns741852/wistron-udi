package com.surgical.vo;

import java.util.List;

import lombok.Data;

@Data
public class PackageDeviceCheckDetailResponse{

    private String configCode;

    private String configName;

    private Long divisionId;

    private Long packageId;

    private String qrcode;
    
    private Integer status;

    private Long trackingId;
    
    private String packageSerialNo;

    private List<PackageDeviceCheckDetailDeviceTypeResponse> deviceTypes;
}
