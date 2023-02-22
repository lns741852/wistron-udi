package com.surgical.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SterilizedBatchDetailResponseVo{

    Long id;

    String name;

    String sterilizer;

    Integer status;
    
    Integer qty;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date finishTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    List<MonitorItem> monitorItems;
    
    
}
