package com.surgical.enums;

public enum ActiveStatus {

    DISABLE(0), // 關、停用
    ENABLE(1)   // 開、啟用
    ;

    private Integer value;

    private ActiveStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
