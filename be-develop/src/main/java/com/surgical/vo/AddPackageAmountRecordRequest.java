package com.surgical.vo;

import lombok.Data;

@Data
public class AddPackageAmountRecordRequest{
    private Long id;
    private Integer status; 
    private Long divisionId;
    private String configName;
    private String configCode;
    private String orderBy;
}
