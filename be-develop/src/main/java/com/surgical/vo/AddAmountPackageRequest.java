package com.surgical.vo;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddAmountPackageRequest{
    private Long id;    //Package ID
    @NotNull(message = "serial number must not be null")
    private String serialNo;
    private String position;
}
