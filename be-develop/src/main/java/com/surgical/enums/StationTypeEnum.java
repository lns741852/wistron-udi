package com.surgical.enums;

public enum StationTypeEnum{

    PACKING(0L, "packing"),
    STERILIZATION(1L, "sterilization"),
    SUPPLY(2L, "supply");

    private Long id;
    
    private String value;

    private StationTypeEnum(Long id, String value){
        this.id = id;
        this.value = value;
    }
    
    public Long getId() {
        return id;
    }

    public String getValue(){
        return value;
    }
}
