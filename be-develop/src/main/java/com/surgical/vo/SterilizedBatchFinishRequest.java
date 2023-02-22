package com.surgical.vo;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SterilizedBatchFinishRequest{

    @NotNull(message = "SterilizedBatch id cannot be null")
    private Long id;

    @NotNull(message = "isSuccess cannot be null")
    private Boolean isSuccess;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;
    
    @NotNull(message = "finishTime cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;
    
    @Valid
    private List<SterilizedBatchFinishMonitorItem> monitorItems;
    
    
}
