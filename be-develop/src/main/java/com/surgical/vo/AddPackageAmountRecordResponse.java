package com.surgical.vo;

import java.util.List;

import lombok.Data;

@Data
public class AddPackageAmountRecordResponse{
    private Long id; // add package amount record id
    private Integer status; 
    private Long configId;
    private Long divisionId;
    private String configName;
    private Integer qty;
    private Integer packagedQty;
    private String configCode;
    private List<PackageInfo> packages;
}
