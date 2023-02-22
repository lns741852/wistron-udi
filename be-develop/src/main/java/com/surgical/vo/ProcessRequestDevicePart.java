package com.surgical.vo;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ProcessRequestDevicePart{

    @NotNull(message = "deviceTypdId cannot be null")
    private Long deviceTypeId;

    @NotNull(message = "deviceIds cannot be null")
    private List<Long> deviceIds;
}
