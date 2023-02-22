package com.surgical.repositories;

import com.surgical.entities.Admin;
import com.surgical.vo.UserListResponse;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>{

    public Optional<Admin> findByAccountAndPassword(String account, String password);
    
    public Optional<Admin> findByAccountAndId(String account, Long id);

    public Optional<Admin> findByAccount(String account);
    
    @Query(value = "SELECT NEW com.surgical.vo.UserListResponse(a.id AS id, a.name AS name, p.name AS roleName, a.lastLoginTime AS lastLoginTime, a.createTime AS createTime ) "
        + "FROM Admin a LEFT JOIN Privilege p ON p.id = a.privilege "
        + "WHERE a.name LIKE CONCAT('%', COALESCE(:userName, ''),'%') "
        + "AND p.name LIKE CONCAT('%', COALESCE(:roleName, ''),'%') ")
    public Page<UserListResponse> findAdminByPrivilegeNameAndName(@Param("userName") String userName, @Param("roleName") String roleName, Pageable pageable);
}
