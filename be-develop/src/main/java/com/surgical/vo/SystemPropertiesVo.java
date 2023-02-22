package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SystemPropertiesVo{
    private Long id;
    private String name;
    private String value;
    private String description;
    private String updater;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    public SystemPropertiesVo(Long id, String name, String value, String description, String updater, Date updateTime){
        super();
        this.id = id;
        this.name = name;
        this.value = value;
        this.description = description;
        this.updater = updater;
        this.updateTime = updateTime;
    }
}
