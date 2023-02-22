package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SterilizedBatchVo{
    private Long id;// sterilized_batch_id
    private String name;
    private String sterilizer;
    private Integer status;
    private Integer qty;
    private Date finishTime;
    private Date startTime;
    private Date createTime;
    
}
