package com.surgical.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class PackageConfigInfoResponseVo{
    private Long configId;
    
    private String configCode;
    
    private String configName;
    
    private Long divisionId;
    
    private Long totalQty;

    private Long stationQty;

    private List<DeviceTypeInfoVo> deviceTypes;

    private Integer deviceTypeQty;

    private Integer deviceQty;
}
