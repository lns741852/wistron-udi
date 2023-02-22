package com.surgical.vo;

import java.util.List;

import lombok.Data;

@Data
public class DeviceTypeInfoVo{

    private Long typeId;

    private Integer qty;

    private String nameScientific;
    
    private String name;

    private String spec;

    private String desc;

    private List<DeviceImageVo> images;
}
