package com.surgical.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public class DeliverBatchListInfoVo implements Serializable{

    private Long id;

    private String title;

    private Integer status;

    private Long fromStationId;

    private Long toStationId;

    private Date createTime;

    private Long totalQty;

    private Long waitForReceiveQty;
}
