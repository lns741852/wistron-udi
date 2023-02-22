package com.surgical.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class DeviceCheckGetInfoVo{

    private Long id; //deviceId

    private Long typeId;

    private Integer status;

    private Integer divisionId;

    private String name;

    private String manufactureId;

    private String brand;

    private String nameScientific;

    private String qrcode;
    
    private String udi;

    private List<DeviceTypeImagesVo> images; //只顯示主圖 
}
