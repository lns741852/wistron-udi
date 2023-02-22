package com.surgical.vo;

import lombok.Data;

@Data
public class SterilizerVo{

    private String sterilizer;

    private Long sterilizedPackages;

    private Long sterilizedCount;

    private Double usageRate;

    public SterilizerVo(String sterilizer, Long sterilizedPackages, Long sterilizedCount){
        super();
        this.sterilizer = sterilizer;
        this.sterilizedPackages = sterilizedPackages;
        this.sterilizedCount = sterilizedCount;
    }
}
