package com.surgical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.surgical.entities.FuncPermission;
import com.surgical.vo.FuncPermissionAndPrivilegeDto;

@Repository
public interface FuncPermissionRepository extends JpaRepository<FuncPermission, Long>{
    
    @Query(value = "SELECT new com.surgical.vo.FuncPermissionAndPrivilegeDto(fp, p) "
                    + "FROM FuncPermission fp "
                    + "LEFT JOIN PrivilegeFuncPermissionMapping pfpm ON pfpm.funcPermissionId = fp.id "
                    + "LEFT JOIN Privilege p ON p.id = pfpm.privilegeId "
                    + "ORDER BY fp.id, p.id ")
    public List<FuncPermissionAndPrivilegeDto> getFuncPermissionAndPrivilegeList();
    
    public List<FuncPermission> findByIdIn(List<Long> ids);
}
