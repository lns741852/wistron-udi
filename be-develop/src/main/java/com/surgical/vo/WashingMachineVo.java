package com.surgical.vo;

import lombok.Data;

@Data
public class WashingMachineVo{

    private String washingMachine;

    private Long washedPackages;

    private Long washedCount;

    private Double usageRate;

    public WashingMachineVo(String washingMachine, Long washedPackages, Long washedCount){
        super();
        this.washingMachine = washingMachine;
        this.washedPackages = washedPackages;
        this.washedCount = washedCount;
    }
}
