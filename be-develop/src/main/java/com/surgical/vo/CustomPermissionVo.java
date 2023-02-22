package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomPermissionVo{

    Long id;

    String nameCN;

    String nameEN;

    Integer level;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;
}
