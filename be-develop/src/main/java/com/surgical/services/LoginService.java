package com.surgical.services;

import com.surgical.entities.Admin;
import com.surgical.entities.FuncPermission;
import com.surgical.entities.Privilege;
import com.surgical.entities.PrivilegeFuncPermissionMapping;
import com.surgical.exception.AppException;
import com.surgical.repositories.AdminRepository;
import com.surgical.repositories.FuncPermissionRepository;
import com.surgical.repositories.PrivilegeRepository;
import com.surgical.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoginService{

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    PrivilegeRepository privilegeRepository;
    
    @Autowired
    FuncPermissionRepository funcPermsiiionRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public Map<String, Object> login(String account, String password){
        Optional<Admin> existAdmin = adminRepository.findByAccountAndPassword(account, password);
        if (existAdmin.isPresent()){
            Admin admin = existAdmin.get();
            Privilege privilege = admin.getPrivilege();
            List<PrivilegeFuncPermissionMapping> privilegeFuncPermissionMappings = privilege.getPrivilegeFuncPermissionMappings();
            List<Long> permissionIds = privilegeFuncPermissionMappings.stream().map(o -> o.getFuncPermissionId()).collect(Collectors.toList());
            List<String> permissionNames = new ArrayList<String>();
            if (!CollectionUtils.isEmpty(permissionIds)){
                List<FuncPermission> funcPermissions = funcPermsiiionRepository.findByIdIn(permissionIds);
                permissionNames = funcPermissions.stream().map(o -> o.getNameEN()).collect(Collectors.toList());
            }
            Map<String, Object> result = new HashMap<>();
            result.put("accountId", admin.getId());
            result.put("account", admin.getAccount());
            result.put("name", admin.getName());
            result.put("roleId", admin.getPrivilegeId());
            result.put("roleName", privilege.getName());
            result.put("roleLevel", privilege.getPrivilegeLevel());
            result.put("permissions", permissionNames);
            result.put("authToken", jwtTokenProvider.generateToken(admin.getId().toString(), admin.getAccount()));
            admin.setLastLoginTime(new Date());
            adminRepository.save(admin);
            return result;
        }else{
            throw new AppException("Invalid account and password");
        }
    }
}
