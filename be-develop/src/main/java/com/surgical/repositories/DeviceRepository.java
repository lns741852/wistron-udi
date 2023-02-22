package com.surgical.repositories;

import com.surgical.entities.Device;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device>{
    
    public List<Device> getByIdInAndTypeIdAndStatusInAndDivision(List<Long> ids, Long typeId, List<Integer> statuses, Integer division);
    
    public List<Device> getByIdIn(List<Long> ids);   
    
    @Query(value = "SELECT SUM(CASE WHEN d.status IN(0,1,2,3,4,6,7) THEN 1 ELSE 0 END) AS totalQty, SUM(CASE WHEN d.status = 9 THEN 1 ELSE 0 END) AS scrappedQty, " + 
        "SUM(CASE WHEN d.status = 0 THEN 1 ELSE 0 END) AS unusedQty, SUM(CASE WHEN d.status = 4 THEN 1 ELSE 0 END) AS repairQty, " + 
        "SUM(CASE WHEN d.status IN(1,2,3) THEN 1 ELSE 0 END) AS usedQty " + 
        "FROM Device d " + 
        "WHERE typeId = :typeId AND division = COALESCE (:division, division) ")
    public Map<String,Object> countNumbers(@Param("typeId") Long typeId, @Param("division") Integer division);

    @Query("SELECT d.qrcode AS qrcode FROM Device d WHERE d.qrcode IN :qrcodeQueryList ")
    public List<String> queryByQrcodeIn(@Param("qrcodeQueryList") List<String> qrcodeQueryList);
    
    public List<Device> findAllByIdInAndTypeIdAndStatusInAndDivision(List<Long> ids, Long typeId, Integer[] status, Integer division);
    
    public Optional<Device> findByQrcode(String qrcode);

    @Query(value = "SELECT DISTINCT dm.id AS modelId, dm.brand AS brand, dm.manufactureId AS manufactureId FROM Device d INNER JOIN DeviceModel dm ON dm.id = d.deviceModelId WHERE d.typeId = :typeId")
    public List<Map<String, Object>> getModelListByTypeIdWithoutInfo(@Param("typeId") Long typeId);
    
    @Query(value = "SELECT d.deviceModelId AS modelId, dm.brand AS brand, dm.manufactureId AS manufactureId, d.division AS division, "
        + "SUM(CASE WHEN d.status IN (0,1,2,3,4) THEN 1 ELSE 0 END) AS totalQty, SUM(CASE WHEN d.status = 0 THEN 1 ELSE 0 END) AS unusedQty, SUM(CASE WHEN d.status IN (1,2,3) THEN 1 ELSE 0 END) AS usedQty, "
        + "SUM(CASE WHEN d.status= 4 THEN 1 ELSE 0 END) AS repairQty, SUM(CASE WHEN d.status = 9 THEN 1 ELSE 0 END) AS scrappedQty "
        + "FROM Device d INNER JOIN DeviceModel dm ON d.deviceModelId = dm.id "
        + "WHERE d.division = COALESCE(:division, division) AND d.typeId = :typeId GROUP BY d.deviceModelId, d.division")
    public List<Map<String, Object>>getModelListByTypeIdWithInfo(@Param("typeId") Long typeId, @Param("division") Long division);
}
