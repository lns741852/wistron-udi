package com.surgical.enums;


public enum SterilizedBatchType{
    
    UNSTERILIZED(0),
    STERILIZING(1),
    COMPLETED(2);
    
    private Integer value;

    private SterilizedBatchType(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}
