package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WashingBatchListVo{

    Long id;

    String name;

    String washingMachine;

    Integer status;

    Integer qty;

    Date finishTime;

    Date startTime;

    Date createTime;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getWashingMachine(){
        return washingMachine;
    }

    public void setWashingMachine(String washingMachine){
        this.washingMachine = washingMachine;
    }

    public Integer getStatus(){
        return status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Integer getQty(){
        return qty;
    }

    public void setQty(Integer qty){
        this.qty = qty;
    }

    public Date getFinishTime(){
        return finishTime;
    }

    public void setFinishTime(Date finishTime){
        this.finishTime = finishTime;
    }

    public Date getStartTime(){
        return startTime;
    }

    public void setStartTime(Date startTime){
        this.startTime = startTime;
    }

    public Date getCreateTime(){
        return createTime;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
}
