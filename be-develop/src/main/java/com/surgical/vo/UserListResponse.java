package com.surgical.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserListResponse{

    private Long id;

    private String name;

    private String roleName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public UserListResponse(Long id, String name, String roleName, Date lastLoginTime, Date createTime){
        super();
        this.id = id;
        this.name = name;
        this.roleName = roleName;
        this.lastLoginTime = lastLoginTime;
        this.createTime = createTime;
    }
}
