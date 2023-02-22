package com.surgical.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CirculationWashingReportVo{

    private Long totalWashedPackages = 0L;

    private Long totalWashedCount = 0L;

    private Long failedWashed = 0L;

    private Double totalUsageRate;

    private List<WashingMachineVo> washingMachines = new ArrayList<WashingMachineVo>();
}
