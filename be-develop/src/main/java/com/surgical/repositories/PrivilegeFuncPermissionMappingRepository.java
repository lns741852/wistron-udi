package com.surgical.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.surgical.entities.PrivilegeFuncPermissionMapping;

@Repository
public interface PrivilegeFuncPermissionMappingRepository extends JpaRepository<PrivilegeFuncPermissionMapping, Long>{

    public Optional<PrivilegeFuncPermissionMapping> findByPrivilegeIdAndFuncPermissionId(Long privilegeId, Long funcPermissionId);

    public List<PrivilegeFuncPermissionMapping> findByPrivilegeId(Long privilegeId);
}
