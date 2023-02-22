package com.surgical.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class PackageRequestVo{

    @NotBlank(message = "configName must not be null")
    @Size(max = 30, message = "configName must be less than 30 characters") 
    private String configName;

    @NotBlank(message = "configCode must not be null")
    @Size(max = 15, message = "configName must be less than 15 characters") 
    private String configCode;

    @NotNull(message = "divisionId must not be null")
    private Long divisionId;

    @NotEmpty(message = "deviceTypes must not be null or empty")
    private List<DeviceTypeVo> deviceTypes;
    
    private Long stationId;
    
    private Long configId;
}
