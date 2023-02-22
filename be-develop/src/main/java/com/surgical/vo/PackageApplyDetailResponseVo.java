package com.surgical.vo;

import java.util.List;

import lombok.Data;

@Data
public class PackageApplyDetailResponseVo{

    private Long id;

    private Long configId;

    private Integer status;

    private Integer applyQty;

    private Integer packagedQty;
    
    private List<PackageInfo> packages;
}
