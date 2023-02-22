package com.surgical.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurgicalApplyGrantDetailInfoVo{

    private Long applyId;

    private String operatingNumber;

    private Long stationId;

    private Long divisionId;
    
    private Integer status;

    private String medicalRecordNumber;

    private String surgeryName;
    
    private String doctor;

    private String operatingRoom;

    private String operatingDate;

    private Integer operatingOrder;
    
    private Date createTime; 
    
    private List<OrderVo> orders;

    
    public Long getApplyId(){
        return applyId;
    }

    
    public void setApplyId(Long applyId){
        this.applyId = applyId;
    }

    
    public String getOperatingNumber(){
        return operatingNumber;
    }

    
    public void setOperatingNumber(String operatingNumber){
        this.operatingNumber = operatingNumber;
    }

    
    public Long getStationId(){
        return stationId;
    }

    
    public void setStationId(Long stationId){
        this.stationId = stationId;
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

    
    public String getMedicalRecordNumber(){
        return medicalRecordNumber;
    }

    
    public void setMedicalRecordNumber(String medicalRecordNumber){
        this.medicalRecordNumber = medicalRecordNumber;
    }

    
    public String getSurgeryName(){
        return surgeryName;
    }

    
    public void setSurgeryName(String surgeryName){
        this.surgeryName = surgeryName;
    }

    
    public String getDoctor(){
        return doctor;
    }

    
    public void setDoctor(String doctor){
        this.doctor = doctor;
    }

    
    public String getOperatingRoom(){
        return operatingRoom;
    }

    
    public void setOperatingRoom(String operatingRoom){
        this.operatingRoom = operatingRoom;
    }

    
    public String getOperatingDate(){
        return operatingDate;
    }

    
    public void setOperatingDate(String operatingDate){
        this.operatingDate = operatingDate;
    }

    
    public Integer getOperatingOrder(){
        return operatingOrder;
    }

    
    public void setOperatingOrder(Integer operatingOrder){
        this.operatingOrder = operatingOrder;
    }

    
    public Date getCreateTime(){
        return createTime;
    }

    
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    
    public List<OrderVo> getOrders(){
        return orders;
    }

    
    public void setOrders(List<OrderVo> orders){
        this.orders = orders;
    }
}
