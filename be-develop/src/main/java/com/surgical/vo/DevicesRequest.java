package com.surgical.vo;

import java.util.List;


public class DevicesRequest{
    
    private Long typeId;
    private Integer updater;
    private List<CreateDevice> devices;
    
    public Long getTypeId(){
        return typeId;
    }
    
    public void setTypeId(Long typeId){
        this.typeId = typeId;
    }
    
    public Integer getUpdater(){
        return updater;
    }

    public void setUpdater(Integer updater){
        this.updater = updater;
    }

    public List<CreateDevice> getDevices(){
        return devices;
    }
    
    public void setDevices(List<CreateDevice> devices){
        this.devices = devices;
    }
}
