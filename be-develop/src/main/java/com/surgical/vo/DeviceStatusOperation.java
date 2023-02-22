package com.surgical.vo;

public class DeviceStatusOperation{

    private Long deviceId;

    private Integer updater;

    private String description;

    public Long getDeviceId(){
        return deviceId;
    }

    public void setDeviceId(Long deviceId){
        this.deviceId = deviceId;
    }

    public Integer getUpdater(){
        return updater;
    }

    public void setUpdater(Integer updater){
        this.updater = updater;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
