package com.surgical.vo;

import java.util.Map;

import lombok.Data;

@Data
public class DeviceTypesQuantityInfo{

    Integer deviceQty;

    Map<Long, Integer> deviceTypeIdAndQuantity;
}
