package com.surgical.enums;

public enum DeliverBatchStatus {

    DELIVERING(0),
    COMPLETE(1)
    ;

    private Integer value;

    private DeliverBatchStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
