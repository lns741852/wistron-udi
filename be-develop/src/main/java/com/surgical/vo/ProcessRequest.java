package com.surgical.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ProcessRequest{
    @NotNull(message = "package id must not be null")
    private Long packageId;

    @NotBlank(message = "deviceBoxQrcode cannot be empty")
    private String deviceBoxQrcode;

    @NotNull(message = "applyDeviceList must not be null")
    private List<ProcessRequestDevicePart> applyDeviceList;
}
