package com.surgical.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FuncPermissionAndRolesVo{

    Long id;

    String nameCN;

    String nameEN;

    Integer level;

    List<CustomRoleVo> roles = new ArrayList<CustomRoleVo>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;
}
