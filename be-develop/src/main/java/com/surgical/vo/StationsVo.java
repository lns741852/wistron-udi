package com.surgical.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationsVo{

    Long packing;

    Long sterilization;

    Long circulation;

    Long supply;
}
