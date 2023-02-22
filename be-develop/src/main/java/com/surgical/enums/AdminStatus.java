package com.surgical.enums;

public enum AdminStatus{

    NORMAL(0),
    DISABLE(1),
    DELETED(2);

    private Integer value;

    private AdminStatus(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}
