package com.surgical.enums;

public enum SurgicalApplyStatus{
    
    APPLY(0),   // 0:申請中; 
    REVIEW(1),  // 1:已審核;
    RECEIVED(2), // 2:已領取;
    USING(3),   // 3:使用中;
    DONE(4),     // 4:使用完成
    CIRCULATE_DONE(5), // 5:回收清點完成
    CANCEL_APPLY(9) // 9:取消申請
    ;
    
    private Integer value;

    private SurgicalApplyStatus(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}
