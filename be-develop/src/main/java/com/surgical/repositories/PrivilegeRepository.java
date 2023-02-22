package com.surgical.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.surgical.entities.Privilege;
import com.surgical.vo.PrivilegeAndFuncPermissionDto;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long>{
    
    @Query(value = "SELECT new com.surgical.vo.PrivilegeAndFuncPermissionDto(p, fp) "
                    + "FROM Privilege p "
                    + "LEFT JOIN PrivilegeFuncPermissionMapping pfpm ON pfpm.privilegeId = p.id "
                    + "LEFT JOIN FuncPermission fp ON fp.id = pfpm.funcPermissionId "
                    + "ORDER BY p.id, fp.id ")
    public List<PrivilegeAndFuncPermissionDto> getPrivilegeAndFuncPermissionList();
    
    public Optional<Privilege> findByName(String name);
    
}
