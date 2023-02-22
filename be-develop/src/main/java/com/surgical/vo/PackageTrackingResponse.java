package com.surgical.vo;

import java.util.List;

import lombok.Data;

@Data
public class PackageTrackingResponse{

    private Long id;

    private String serialNo;

    private String packageCode;

    private Long configId;

    private String configName;

    private String configCode;

    private Long divisionId;

    private List<TrackingRecordInfo> trackingRecords;

    public PackageTrackingResponse(Long id, String serialNo, String packageCode, Long configId, String configName, String configCode, Long divisionId){
        super();
        this.id = id;
        this.serialNo = serialNo;
        this.packageCode = packageCode;
        this.configId = configId;
        this.configName = configName;
        this.configCode = configCode;
        this.divisionId = divisionId;
    }
    
    
}
