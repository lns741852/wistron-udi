package com.surgical.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SterilizationReportVo{

    private Long totalSterilizedPackages = 0L;

    private Long totalSterilizedCount = 0L;

    private Long failedSterilized = 0L;

    private Double totalUsageRate;

    private List<SterilizerVo> sterilizers = new ArrayList<SterilizerVo>();
}
