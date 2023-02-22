package com.surgical.vo;

import java.util.List;

import lombok.Data;

@Data
public class PackageDeviceCheckDto{
    Integer step;
    Boolean sterilizedSuccess;
    List<DeviceTypeVo> deviceTypes;
}
