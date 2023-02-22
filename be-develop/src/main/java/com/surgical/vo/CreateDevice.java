package com.surgical.vo;

public class CreateDevice{

    private String udi;

    private String qrcode;

    private Float cost;

    private Integer division;

    private Long deviceModelId;

    private DeviceModelDto deviceModel;

    public String getUdi(){
        return udi;
    }

    public void setUdi(String udi){
        this.udi = udi;
    }

    public String getQrcode(){
        return qrcode;
    }

    public void setQrcode(String qrcode){
        this.qrcode = qrcode;
    }

    public Float getCost(){
        return cost;
    }

    public void setCost(Float cost){
        this.cost = cost;
    }

    public Integer getDivision(){
        return division;
    }

    public void setDivision(Integer division){
        this.division = division;
    }

    public Long getDeviceModelId(){
        return deviceModelId;
    }

    public void setDeviceModelId(Long deviceModelId){
        this.deviceModelId = deviceModelId;
    }

    public DeviceModelDto getDeviceModel(){
        return deviceModel;
    }

    public void setDeviceModel(DeviceModelDto deviceModel){
        this.deviceModel = deviceModel;
    }
}
