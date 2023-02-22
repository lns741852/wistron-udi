package com.surgical.vo;

import java.util.Date;

import lombok.Data;

@Data
public class DeviceListVo{
    
    private Long id; //device_id
    private Integer status;
    private Date createTime;
    private Date usedTime;
    private String udi;
    private Float cost;
    private String qrcode;
    private Integer division;
    private Long usedCount;
    private Date scrapTime;
}
