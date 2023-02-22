package com.surgical.entities;


import lombok.Data;

@Data
public class PackageGetInfoRequest{
    private String qrcode;
    private String packageCode;
    private Boolean devices = false;
}
