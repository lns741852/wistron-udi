package com.surgical.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class DeviceDetailRequest{
    @NotBlank(message = "qrcode cannot be null or empty")
    private String qrcode;
    
    private Boolean history = false;

    @Pattern(regexp = "^\\d{4}[\\-](0[1-9]|1[012])[\\-](0[1-9]|[12][0-9]|3[01])$", message = "pattern : yyyy-MM-dd")
    private String start;

    @Pattern(regexp = "^\\d{4}[\\-](0[1-9]|1[012])[\\-](0[1-9]|[12][0-9]|3[01])$", message = "pattern : yyyy-MM-dd")
    private String end;
}
