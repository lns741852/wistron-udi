package com.surgical.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.surgical.entities.FuncPermission;
import com.surgical.entities.Privilege;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FuncPermissionAndPrivilegeDto{

    FuncPermission funcPermission;

    Privilege privilege;
    
    public FuncPermissionAndPrivilegeDto(FuncPermission funcPermission, Privilege privilege){
        super();
        this.funcPermission = funcPermission;
        this.privilege = privilege;
    }
}
