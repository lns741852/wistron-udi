package com.surgical.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderVo{

    private Long id;

    private Integer status;

    private List<OrderDetailVo> details;
    
    private List<PackageInfo> packages;

    
    public Long getId(){
        return id;
    }

    
    public void setId(Long id){
        this.id = id;
    }

    
    public Integer getStatus(){
        return status;
    }

    
    public void setStatus(Integer status){
        this.status = status;
    }

    
    public List<OrderDetailVo> getDetails(){
        return details;
    }

    
    public void setDetails(List<OrderDetailVo> details){
        this.details = details;
    }

    
    public List<PackageInfo> getPackages(){
        return packages;
    }

    
    public void setPackages(List<PackageInfo> packages){
        this.packages = packages;
    }
}
