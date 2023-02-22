package com.surgical.enums;

import java.util.Optional;

public enum PackageOrderStatus{

    NOT_COLLECT(0, "未領用"),
    COLLECTED(1, "已領用"),
    IN_USE(2, "使用中"),
    FINISH(3, "使用完成"),
    CIRCULATE_CHECK_DONE(4, "回收清點完成"),
    CANCEL_ORDER(9, "取消訂單");

    private Integer value;

    private String comment;

    private PackageOrderStatus(Integer value, String comment){
        this.value = value;
        this.comment = comment;
    }

    public Integer getValue(){
        return value;
    }

    public String getComment(){
        return comment;
    }

    public static PackageOrderStatus convertByValue(final Integer value){
        for(PackageOrderStatus packageOrderStatus : PackageOrderStatus.values()){
            if (packageOrderStatus.getValue() == value){
                return packageOrderStatus;
            }
        }
        return null;
    }
}
