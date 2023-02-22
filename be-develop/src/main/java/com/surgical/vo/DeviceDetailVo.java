package com.surgical.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceDetailVo{

    private Long id;

    private String name;

    private String manufactureId;

    private String brand;

    private String nameScientific;

    private String spec;

    private String desc;

    private List<DeviceImageVo> images;

    private Integer status;

    private String packageSerialNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date usedTime;

    private String udi;

    private String qrcode;

    private Float cost;

    private Integer division;

    private Long usedCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date scrapTime;

    private Long typeId;

    private Long deviceId;

    private List<DeviceRepairRecordVo> repairRecords;
    
    private Long repairCount = 0L;

    private List<DeviceDetailUseRecord> useRecords;
}
