package com.surgical.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackageInfo{

    private Long id;//package id

    private String qrcode;

    private Long configId;

    private String configName;

    private String configCode;

    private Long divisionId;

    private Integer status;

    private Date expireTime;

    private String packageCode;

    private String serialNo;

    private String position;

    private List<DeviceDetailVo> devices;

    //SURGICAL_APPLY detail
    private Long orderMappingId;

    private Long trackingId;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getQrcode(){
        return qrcode;
    }

    public void setQrcode(String qrcode){
        this.qrcode = qrcode;
    }

    public Long getConfigId(){
        return configId;
    }

    public void setConfigId(Long configId){
        this.configId = configId;
    }

    public String getConfigName(){
        return configName;
    }

    public void setConfigName(String configName){
        this.configName = configName;
    }

    public String getConfigCode(){
        return configCode;
    }

    public void setConfigCode(String configCode){
        this.configCode = configCode;
    }

    public Long getDivisionId(){
        return divisionId;
    }

    public void setDivisionId(Long divisionId){
        this.divisionId = divisionId;
    }

    public Integer getStatus(){
        return status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Date getExpireTime(){
        return expireTime;
    }

    public void setExpireTime(Date expireTime){
        this.expireTime = expireTime;
    }

    public String getPackageCode(){
        return packageCode;
    }

    public void setPackageCode(String packageCode){
        this.packageCode = packageCode;
    }

    public String getSerialNo(){
        return serialNo;
    }

    public void setSerialNo(String serialNo){
        this.serialNo = serialNo;
    }

    public String getPosition(){
        return position;
    }

    public void setPosition(String position){
        this.position = position;
    }

    public List<DeviceDetailVo> getDevices(){
        return devices;
    }

    public void setDevices(List<DeviceDetailVo> devices){
        this.devices = devices;
    }

    public Long getOrderMappingId(){
        return orderMappingId;
    }

    public void setOrderMappingId(Long orderMappingId){
        this.orderMappingId = orderMappingId;
    }

    public Long getTrackingId(){
        return trackingId;
    }

    public void setTrackingId(Long trackingId){
        this.trackingId = trackingId;
    }
}
