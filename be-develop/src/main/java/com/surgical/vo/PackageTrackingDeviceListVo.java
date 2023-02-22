package com.surgical.vo;

import java.util.List;

import lombok.Data;

@Data
public class PackageTrackingDeviceListVo{

    private Long id;

    private Long typeId;

    private String name;

    private String nameScientific;

    private String spec;

    private String desc;

    private String brand;

    private String manufactureId;

    private Integer status;

    private String udi;

    private String qrcode;

    private Float cost;

    List<DeviceImageVo> images;
}
