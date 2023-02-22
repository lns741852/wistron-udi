package com.surgical.enums;


public enum PrivilegeStatus{
    ACTIVATED(0),
    DEACTIVATED(1),
    DEPRECATED(2);
    
    private Integer value;

    private PrivilegeStatus(Integer value){
        this.value = value;
    }
    
    public Integer getValue(){
        return value;
    }
}
