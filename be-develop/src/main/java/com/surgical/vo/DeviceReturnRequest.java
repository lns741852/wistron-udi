package com.surgical.vo;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class DeviceReturnRequest{
    @NotEmpty(message = "qrcode cannot be null or empty")
    private String qrcode;
}
