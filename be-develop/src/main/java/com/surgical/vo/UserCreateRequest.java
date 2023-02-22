package com.surgical.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserCreateRequest{

    private Long id;

    private String account;

    @NotBlank(message = "userName cannot be null or empty")
    private String userName;

    @NotNull(message = "roleId cannot be null")
    private Integer roleId;
}
