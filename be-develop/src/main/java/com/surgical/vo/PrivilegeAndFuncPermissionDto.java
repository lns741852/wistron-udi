package com.surgical.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.surgical.entities.FuncPermission;
import com.surgical.entities.Privilege;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrivilegeAndFuncPermissionDto{

    Privilege privilege;

    FuncPermission funcPermission;

    public PrivilegeAndFuncPermissionDto(Privilege privilege, FuncPermission funcPermission){
        super();
        this.privilege = privilege;
        this.funcPermission = funcPermission;
    }
}
