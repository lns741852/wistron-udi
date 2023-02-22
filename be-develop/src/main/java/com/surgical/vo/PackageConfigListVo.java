package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackageConfigListVo{
    
    private Long configId;
    private Long divisionId;
    private String configName;
    private Integer isActive;
    private Date createTimeDate;
    private String configCode;
    private Integer packageQty;
    private Integer totalUsedCount;
    private Integer inStockQty;
}
