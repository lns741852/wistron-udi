package com.surgical.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusVo{

    Long available;
    
    Long expired;

    Long process;

    Long used;
}
