package com.surgical.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.surgical.entities.DeviceType;

@Repository
public interface DeviceTypeRepository extends JpaRepository<DeviceType, Long>, JpaSpecificationExecutor<DeviceType>{

    public Optional<DeviceType> findById(Long id);

    @Query(value = "SELECT d.id AS id, COUNT(de.id) AS qty FROM Device de INNER JOIN Division d ON de.division = d.id WHERE de.typeId = :typeId AND de.status NOT IN (9,99) GROUP BY de.division ")
    public List<Map<String, Object>> queryDeviceQuantityByTypeId(@Param("typeId") Long typeId);

    @Query(value = "SELECT dt FROM DeviceType dt INNER JOIN PackageConfigDetail pcd ON pcd.deviceTypeId = dt.id WHERE pcd.packageConfigId = :configId")
    public List<DeviceType> getAllTypesByConfigId(@Param("configId") Long configId);
    
    public Optional<DeviceType> findByNameScientificAndSpec(String nameScientific, String spec);
    
    public Optional<DeviceType> findByNameScientificAndSpecAndIdIsNot(String nameScientific, String spec, Long id);
}
