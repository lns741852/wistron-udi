package com.surgical.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceBoxDetailVo{

    private Long id;

    private String qrcode;

    private String udi;

    private Float cost;
    
    private Integer status;
    
    private Long usedCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date usedTime;

    private List<DeviceBoxRepairVo> records;
    
    private Long repairCount = 0L;
}
