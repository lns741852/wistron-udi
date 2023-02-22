package com.surgical.enums;

public enum StatusFlag {

    NORMAL(0),
    SUSPEND(1),
    DELETE(2)
    ;

    private Integer value;

    private StatusFlag(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
