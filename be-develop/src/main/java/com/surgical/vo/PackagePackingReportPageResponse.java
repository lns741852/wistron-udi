package com.surgical.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PackagePackingReportPageResponse{

    private String configCode;

    private String packageName;

    private Long divisionId;

    private Long packageQty;

    private Long packingCount;
}
