package com.surgical.enums;

public enum ComponentStatusFlag {
    //for device status
    NORMAL(0),
    IN_PACKAGE(1),
    REPLACED(2),
    RECEIVE_SCAN_DONE(3),
    REPAIR(4),
    REPAIR_DONE(5),
    UNSCANABLE(6),
    MISSING(7),
    SCRAPPED(9),
    DELETE(99),
    
    //for division status
    DIVISION_NORMAL(0),
    DIVISION_STOPPED(1),
    DIVISION_DELETE(2),
    
    //for add_package_amount_record status
    APAR_ALLCANCEL(0),
    APAR_APPLY(1),
    APAR_RROCESSING(2),
    APAR_DONE(3)
    ;
    private Integer value;

    private ComponentStatusFlag(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
