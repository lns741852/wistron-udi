package com.surgical.enums;


public enum PackageStatus{
    
    APPLY(-1), //申請中
    PACK_DONE(0), //包盤完成
    PACK_TO_STER(1), //包盤完成交滅菌
    STER_RECV(2), //滅菌中心領取
    STER_PROCESS(3), //滅菌中
    STER_DONE(4), //滅菌完成
    STER_TO_STOCK(5), //滅菌滅好交庫存
    IN_STOCK(6), //庫存
    DISTRIBUTE(7), //發放完成
    IN_USE(8), //使用中
    USE_DONE(9), //使用完成
    CIRCULATION(10), //回收處理
    CIRCU_TO_PACK(11), //清洗交付包盤
    RECV_FROM_CIRCU_OR_STOCK(12), //包盤接收清洗/庫存
    RE_STERILIZE(13), //重新滅菌
    STER_FAIL(14), //滅菌失敗
    WASH_FAIL(15),//清洗失敗
    RECV_FROM_EXPIRED_STER(16),//滅菌過期領取
    PACK_INDICATOR_FAIL(17), //包內指示劑失敗
    UNPACK(99); //拆盤

    private Integer value;

    private PackageStatus(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}
