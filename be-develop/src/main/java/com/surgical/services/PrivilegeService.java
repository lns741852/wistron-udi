package com.surgical.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.surgical.entities.Privilege;
import com.surgical.entities.PrivilegeFuncPermissionMapping;
import com.surgical.enums.FuncPermissionCode;
import com.surgical.enums.PrivilegeLevel;
import com.surgical.enums.PrivilegeStatus;
import com.surgical.exception.AppException;
import com.surgical.repositories.AdminRepository;
import com.surgical.repositories.FuncPermissionRepository;
import com.surgical.repositories.PrivilegeFuncPermissionMappingRepository;
import com.surgical.repositories.PrivilegeRepository;
import com.surgical.vo.CreateRoleInfo;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
public class PrivilegeService{
    
    @Autowired
    PrivilegeFuncPermissionMappingRepository privilegeFuncPermissionMappingRepository;
    
    @Autowired
    PrivilegeRepository privilegeRepository;
    
    @Autowired
    FuncPermissionRepository funcPermissionRepository;
    
    @Autowired
    AdminRepository adminRepository;
    
    public void createOrUpdateRole(CreateRoleInfo info, Long accountId){
        if (null != info.getId() && info.getId() != 0){
            //UPDATE
            Optional<Privilege> privilegeOpt = privilegeRepository.findById(info.getId());
            Privilege privilege = privilegeOpt.orElseThrow(() -> new AppException("查無此角色"));
            privilege.setName(info.getRoleName());
            privilege.setUpdater(accountId.intValue());
            privilege.setUpdateTime(new Date());
            privilegeRepository.save(privilege);
            List<PrivilegeFuncPermissionMapping> oriPermissions = privilegeFuncPermissionMappingRepository.findByPrivilegeId(info.getId());
            updatePrivilegeFuncPermissionMapping(info, oriPermissions);
        }else{
            //INSERT
            Optional<Privilege> privilegeOpt = privilegeRepository.findByName(info.getRoleName());
            if (privilegeOpt.isPresent()){
                throw new AppException("角色名稱已重複，請重新輸入。");
            }
            Date now = new Date();
            Privilege privilege = new Privilege();
            privilege.setName(info.getRoleName());
            privilege.setStatus(PrivilegeStatus.ACTIVATED.getValue());
            privilege.setPrivilegeLevel(PrivilegeLevel.NORMAL.getValue());
            privilege.setCreateTime(now);
            privilege.setUpdateTime(now);
            privilege.setUpdater(accountId.intValue());
            Long privilegeId = privilegeRepository.saveAndFlush(privilege).getId();
            insertPrivilegeFuncPermissionMapping(privilegeId, info);
        }
    }
    
    private void insertPrivilegeFuncPermissionMapping(Long privilegeId, CreateRoleInfo info){
        List<PrivilegeFuncPermissionMapping> insertPermissions = new ArrayList<PrivilegeFuncPermissionMapping>();
        for(Long func : info.getFuncPermissions()){
            if (func == null || FuncPermissionCode.valueOf(func.intValue()) == null){
                log.error("新增角色權限失敗，查無權限代碼：" + func);
                throw new AppException("新增角色權限失敗，查無權限代碼：" + func);
            }
            insertPermissions.add(genPrivilegeFuncPermissionMapping(privilegeId, func));
        }
        privilegeFuncPermissionMappingRepository.saveAll(insertPermissions);
    }

    private void updatePrivilegeFuncPermissionMapping(CreateRoleInfo info, List<PrivilegeFuncPermissionMapping> oriPermissions){
        Map<Long, PrivilegeFuncPermissionMapping> oriPermissionMap = oriPermissions.stream().collect(Collectors.toMap(PrivilegeFuncPermissionMapping::getFuncPermissionId, p -> p));
        List<PrivilegeFuncPermissionMapping> insertPermissions = new ArrayList<PrivilegeFuncPermissionMapping>();
        for(Long func : info.getFuncPermissions()){
            if (func == null || FuncPermissionCode.valueOf(func.intValue()) == null){
                log.error("編輯角色權限失敗，查無權限代碼：" + func);
                throw new AppException("編輯角色權限失敗，查無權限代碼：" + func);
            }
            if (oriPermissionMap.get(func.longValue()) == null){
                insertPermissions.add(genPrivilegeFuncPermissionMapping(info.getId(), func));
            }else{
                oriPermissionMap.remove(func.longValue());
            }
        }
        List<PrivilegeFuncPermissionMapping> removePermissions = new ArrayList<PrivilegeFuncPermissionMapping>(oriPermissionMap.values());
        privilegeFuncPermissionMappingRepository.saveAll(insertPermissions);
        privilegeFuncPermissionMappingRepository.deleteAll(removePermissions);
    }
    
    private PrivilegeFuncPermissionMapping genPrivilegeFuncPermissionMapping(Long privilegeId, Long funcPermissionId) {
        PrivilegeFuncPermissionMapping pfpm = new PrivilegeFuncPermissionMapping();
        pfpm.setPrivilegeId(privilegeId);
        pfpm.setFuncPermissionId(funcPermissionId);
        return pfpm;
    }
}
