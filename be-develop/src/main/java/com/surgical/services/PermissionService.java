package com.surgical.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.surgical.entities.FuncPermission;
import com.surgical.entities.Privilege;
import com.surgical.repositories.FuncPermissionRepository;
import com.surgical.vo.CustomRoleVo;
import com.surgical.vo.FuncPermissionAndPrivilegeDto;
import com.surgical.vo.FuncPermissionAndRolesVo;

@Service
public class PermissionService{

    @Autowired
    FuncPermissionRepository funcPermissionRepository;
    
    public List<FuncPermissionAndRolesVo> list(){
        List<FuncPermissionAndRolesVo> funcPermissionAndRolesVos;
        List<FuncPermissionAndPrivilegeDto> funcPermissionAndPrivilegeDtos = funcPermissionRepository.getFuncPermissionAndPrivilegeList();
        if (funcPermissionAndPrivilegeDtos.isEmpty()) {
            funcPermissionAndRolesVos = new ArrayList<FuncPermissionAndRolesVo>();
        } else {
            Map<Long, FuncPermissionAndRolesVo> funcIdAndPermissionRolesMap = new HashMap<Long, FuncPermissionAndRolesVo>();
            for(FuncPermissionAndPrivilegeDto funcPermissionAndPrivilegeDto : funcPermissionAndPrivilegeDtos){
                FuncPermission funcPermission = funcPermissionAndPrivilegeDto.getFuncPermission();
                FuncPermissionAndRolesVo funcPermissionAndRolesVo = funcIdAndPermissionRolesMap.get(funcPermission.getId());
                if (funcPermissionAndRolesVo == null){
                    funcPermissionAndRolesVo = new FuncPermissionAndRolesVo();
                    funcPermissionAndRolesVo.setId(funcPermission.getId());
                    funcPermissionAndRolesVo.setNameCN(funcPermission.getNameCN());
                    funcPermissionAndRolesVo.setNameEN(funcPermission.getNameEN());
                    funcPermissionAndRolesVo.setLevel(funcPermission.getFuncLevel());
                    funcPermissionAndRolesVo.setCreateTime(funcPermission.getCreateTime());
                }
                
                List<CustomRoleVo> customRoleVos = funcPermissionAndRolesVo.getRoles();

                Privilege privilege = funcPermissionAndPrivilegeDto.getPrivilege();
                if (privilege != null){
                    CustomRoleVo customRoleVo = new CustomRoleVo();
                    customRoleVo.setId(privilege.getId());
                    customRoleVo.setName(privilege.getName());
                    customRoleVo.setLevel(privilege.getPrivilegeLevel());
                    customRoleVo.setStatus(privilege.getStatus());
                    customRoleVo.setCreateTime(privilege.getCreateTime());
                    customRoleVos.add(customRoleVo);
                }
                funcPermissionAndRolesVo.setRoles(customRoleVos);
                funcIdAndPermissionRolesMap.put(funcPermission.getId(), funcPermissionAndRolesVo);
            }
            funcPermissionAndRolesVos = new ArrayList<FuncPermissionAndRolesVo>(funcIdAndPermissionRolesMap.values());
        }
        
        return funcPermissionAndRolesVos;
    }
}
