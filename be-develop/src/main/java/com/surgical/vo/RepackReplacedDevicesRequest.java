package com.surgical.vo;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RepackReplacedDevicesRequest{

    @NotNull(message = "id cannot be null")
    private Long id;

    @NotNull(message = "status cannot be null")
    private Integer status;

    @NotNull(message = "newDeviceId cannot be null")
    private Long newDeviceId;
}
