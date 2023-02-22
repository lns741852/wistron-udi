package com.surgical.enums;

public enum PrivilegeLevel{

    ADMIN(1),
    LEADER(2),
    NORMAL(3);

    private Integer value;

    private PrivilegeLevel(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
    
    public static PrivilegeLevel valueOf(Integer value) {
        switch (value) {
            case 1: return ADMIN;
            case 2: return LEADER;
            case 3: return NORMAL;
            default: return null;
        }
    }
}
