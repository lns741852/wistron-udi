package com.surgical.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surgical.entities.Admin;
import com.surgical.enums.AdminStatus;
import com.surgical.exception.AppException;
import com.surgical.repositories.AdminRepository;
import com.surgical.repositories.PrivilegeRepository;
import com.surgical.repositories.SystemPropertiesRepository;
import com.surgical.vo.UserCreateRequest;
import com.surgical.vo.UserListResponse;

@Service
public class UserService{
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private SystemPropertiesRepository systemPropertiesRepository;
    
    @Autowired
    private PrivilegeRepository privilegeRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserCreateRequest request, Long accountId){
        if (request.getId() == null && ( request.getAccount() == null || request.getAccount().isEmpty() )){
            throw new AppException("id、account 擇一帶");
        }
        privilegeRepository.findById(request.getRoleId().longValue()).orElseThrow(() -> new AppException("此 roleId 不存在"));
        Admin admin;
        Date now = new Date();
        if (request.getId() == null){
            admin = new Admin();
            adminRepository.findByAccount(request.getAccount()).ifPresent(x -> {
                throw new AppException("帳號重複");
            });
            admin.setAccount(request.getAccount());
            String defaultPw = systemPropertiesRepository.findValueByName("default_pw");
            if (defaultPw == null || defaultPw.isEmpty()){
                throw new AppException("check systemProperties if default_pw is present");
            }
            admin.setPassword(defaultPw);
            admin.setStatus(AdminStatus.NORMAL.getValue());
            admin.setCreateTime(now);
            admin.setIsDefaultPwd(0);
        }else{
            admin = adminRepository.findById(request.getId()).orElseThrow(() -> new AppException("查無此id"));
        }
        admin.setName(request.getUserName());
        admin.setPrivilegeId(request.getRoleId());
        admin.setUpdateTime(now);
        admin.setUpdater(accountId);
        adminRepository.save(admin);
    }
    
    public Page<UserListResponse> list(String userName, String roleName, Pageable pageable){
        return adminRepository.findAdminByPrivilegeNameAndName(userName, roleName, pageable);
    }
    
}
