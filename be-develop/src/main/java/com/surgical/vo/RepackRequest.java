package com.surgical.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RepackRequest{

    @NotBlank(message = "deviceBoxQrcode cannot be empty")
    private String deviceBoxQrcode;

    @NotNull(message = "devices cannot be null")
    private List<Long> devices;

    @Valid
    @NotNull(message = "replacedDevices cannot be null")
    private List<RepackReplacedDevicesRequest> replacedDevices;
}
