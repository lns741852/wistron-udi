package com.surgical.vo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class SterilizedBatchFinishMonitorItem{

    @NotNull(message = "type cannot be null")
    private Integer type;

    @Size(max = 20, message = "indicator cannot be larger than 20")
    private String indicator;

    private Integer result;
}
