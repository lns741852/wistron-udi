package com.surgical.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.surgical.entities.FuncPermission;
import com.surgical.entities.Privilege;
import com.surgical.repositories.PrivilegeRepository;
import com.surgical.vo.CustomPermissionVo;
import com.surgical.vo.PrivilegeAndFuncPermissionDto;
import com.surgical.vo.PrivilegeAndPermissionsVo;

@Service
public class RoleService{
    @Autowired
    PrivilegeRepository privilegeRepository;
    
    public List<PrivilegeAndPermissionsVo> list(Boolean permissions){
        List<PrivilegeAndPermissionsVo> privilegeAndPermissionsVos;
        if (!permissions){
            privilegeAndPermissionsVos = privilegelist();
        }else{
            List<PrivilegeAndFuncPermissionDto> privilegeAndFuncPermissionDtos = privilegeRepository.getPrivilegeAndFuncPermissionList();
            if (privilegeAndFuncPermissionDtos.isEmpty()){
                privilegeAndPermissionsVos = new ArrayList<PrivilegeAndPermissionsVo>();
            }else{
                Map<Long, PrivilegeAndPermissionsVo> privilegeIdAndFunPermissionsMap = new HashMap<Long, PrivilegeAndPermissionsVo>();
                for(PrivilegeAndFuncPermissionDto privilegeAndFuncPermissionDto : privilegeAndFuncPermissionDtos){
                    Privilege privilege = privilegeAndFuncPermissionDto.getPrivilege();
                    PrivilegeAndPermissionsVo privilegeAndPermissionsVo = privilegeIdAndFunPermissionsMap.get(privilege.getId());
                    if (privilegeAndPermissionsVo == null){
                        privilegeAndPermissionsVo = new PrivilegeAndPermissionsVo();
                        privilegeAndPermissionsVo.setId(privilege.getId());
                        privilegeAndPermissionsVo.setName(privilege.getName());
                        privilegeAndPermissionsVo.setLevel(privilege.getPrivilegeLevel());
                        privilegeAndPermissionsVo.setStatus(privilege.getStatus());
                        privilegeAndPermissionsVo.setCreateTime(privilege.getCreateTime());
                    }
                    List<CustomPermissionVo> customPermissionVos = privilegeAndPermissionsVo.getPermissions();
                    FuncPermission funcPermission = privilegeAndFuncPermissionDto.getFuncPermission();
                    if (funcPermission != null){
                        CustomPermissionVo customPermissionVo = new CustomPermissionVo();
                        customPermissionVo.setId(funcPermission.getId());
                        customPermissionVo.setNameCN(funcPermission.getNameCN());
                        customPermissionVo.setNameEN(funcPermission.getNameEN());
                        customPermissionVo.setLevel(funcPermission.getFuncLevel());
                        customPermissionVo.setCreateTime(funcPermission.getCreateTime());
                        customPermissionVos.add(customPermissionVo);
                    }
                    privilegeAndPermissionsVo.setPermissions(customPermissionVos);
                    privilegeIdAndFunPermissionsMap.put(privilege.getId(), privilegeAndPermissionsVo);
                }
                privilegeAndPermissionsVos = new ArrayList<PrivilegeAndPermissionsVo>(privilegeIdAndFunPermissionsMap.values());
            }
        }
        return privilegeAndPermissionsVos;
    }

    public List<PrivilegeAndPermissionsVo> privilegelist(){
        List<PrivilegeAndPermissionsVo> resultList = new ArrayList<PrivilegeAndPermissionsVo>();
        List<Privilege> privileges = privilegeRepository.findAll();
        for(Privilege privilege : privileges){
            PrivilegeAndPermissionsVo privilegeAndPermissionsVo = new PrivilegeAndPermissionsVo();
            privilegeAndPermissionsVo.setId(privilege.getId());
            privilegeAndPermissionsVo.setName(privilege.getName());
            privilegeAndPermissionsVo.setStatus(privilege.getStatus());
            privilegeAndPermissionsVo.setLevel(privilege.getPrivilegeLevel());
            privilegeAndPermissionsVo.setCreateTime(privilege.getCreateTime());
            privilegeAndPermissionsVo.setPermissions(null);
            resultList.add(privilegeAndPermissionsVo);
        }
        return resultList;
    }
}
