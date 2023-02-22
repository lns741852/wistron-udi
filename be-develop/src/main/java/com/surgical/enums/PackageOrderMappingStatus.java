package com.surgical.enums;

public enum PackageOrderMappingStatus{

    NOT_CHECK(0),       // 未清點
    IN_USE(1),          // 使用中
    FINISHED(2),        // 使用完成
    CIRCULATE_CHECK(3); // 回收清點

    private Integer value;
    
    private PackageOrderMappingStatus(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
    
    public static PackageOrderMappingStatus convertByValue(final int value){
        for(PackageOrderMappingStatus packageOrderMappingStatus : PackageOrderMappingStatus.values()){
            if (packageOrderMappingStatus.value == value){
                return packageOrderMappingStatus;
            }
        }
        return null;
    }
}
