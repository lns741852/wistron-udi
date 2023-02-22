package com.surgical.services;

import com.surgical.entities.Admin;
import com.surgical.enums.StatusFlag;
import com.surgical.exception.AppException;
import com.surgical.repositories.AdminRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminService{

    @Autowired
    private AdminRepository adminRepository;

    public Page<Admin> list(Pageable pageable){
        Page<Admin> result = adminRepository.findAll(pageable);
        return result;
    }
    
    public Optional<Admin> findByAccountAndId(String account, Long accountId){
        Optional<Admin> result = adminRepository.findByAccountAndId(account, accountId);
        return result;
    }

    public void create(Admin admin){
        Date now = new Date();
        admin.setCreateTime(now);
        admin.setUpdateTime(now);
        adminRepository.save(admin);
    }

    public void update(Admin admin){
        if (adminRepository.findById(admin.getId()).isPresent()){
            admin.setUpdateTime(new Date());
            adminRepository.save(admin);
        }else{
            throw new AppException("invalid admin!");
        }
    }

    public void updateStatus(Long id, StatusFlag status, String updater){
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isPresent()){
            Admin existAdmin = admin.get();
            existAdmin.setStatus(status.getValue());
            existAdmin.setUpdater(Long.valueOf(updater));
            existAdmin.setUpdateTime(new Date());
            adminRepository.save(existAdmin);
        }
    }
}
