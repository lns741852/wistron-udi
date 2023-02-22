package com.surgical.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CreateRoleInfo{

    private Long id;

    @NotBlank(message = "roleName cannot be empty or null")
    @Size(max = 32, message = "size of roleName cannot be greater than 32 Characters")
    private String roleName;

    @NotEmpty
    private List<Long> funcPermissions;

    private Integer roleLevel = 3;
}
