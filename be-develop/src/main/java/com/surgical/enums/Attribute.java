package com.surgical.enums;

public enum Attribute{

    WASHING_MACHINE("washingMachine"),
    STERILIZER("sterilizer"),
    PACKAGE_POSITION ("packagePosition");
    private String value;
    
    private Attribute(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
    
    public static Attribute convertByValue(final String value){
        for(Attribute attr : Attribute.values()){
            if (attr.value.equals(value)){
                return attr;
            }
        }
        return null;
    }
}
