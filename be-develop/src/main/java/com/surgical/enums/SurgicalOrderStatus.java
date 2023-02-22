package com.surgical.enums;

public enum SurgicalOrderStatus{

    YET_RECEIVED(0), // 尚未領取
    RECEIVED(1); //已領取

    private Integer value;

    private SurgicalOrderStatus(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}
