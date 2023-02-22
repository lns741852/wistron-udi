package com.surgical.vo;

import lombok.Data;

@Data
public class OrderDetailVo{

    private Long configId;

    private String configCode;

    private String configName;

    private Long configDivisionId;

    private Integer qty;
}
