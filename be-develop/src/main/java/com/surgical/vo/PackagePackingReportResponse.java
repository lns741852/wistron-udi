package com.surgical.vo;

import org.springframework.data.domain.Page;

import lombok.Data;

@Data
public class PackagePackingReportResponse{

    private Long totalPackingCount;

    private Page<PackagePackingReportPageResponse> packageConfigs;
}
