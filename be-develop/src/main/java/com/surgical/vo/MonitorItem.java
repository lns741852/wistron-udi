package com.surgical.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.surgical.entities.SterilizedMonitor;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorItem{

    Integer type;

    String indicator;

    Integer result;

    public MonitorItem(){
    }

    public MonitorItem(SterilizedMonitor sterilizedMonitor){
        this.type = sterilizedMonitor.getType();
        this.indicator = sterilizedMonitor.getIndicator();
        this.result = sterilizedMonitor.getResult();
    }
}
