package com.surgical.enums;

public enum SterilizedBatchStatus{

    FAIL(0),
    SUCCESS(1);

    private Integer value;

    private SterilizedBatchStatus(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}
