package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class TrackingRecordInfo{

    private Long id;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Long creator;

    private String createName;

    public TrackingRecordInfo(Long id, Integer status, Date createTime, Long creator, String createName){
        super();
        this.id = id;
        this.status = status;
        this.createTime = createTime;
        this.creator = creator;
        this.createName = createName;
    }
    
}
