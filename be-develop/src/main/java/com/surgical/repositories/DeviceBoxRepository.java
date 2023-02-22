package com.surgical.repositories;

import com.surgical.entities.DeviceBox;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceBoxRepository extends JpaRepository<DeviceBox, Long>{
    
    public Optional<DeviceBox> findByQrcode(String qrcode);
    
    public Optional<DeviceBox> findFirstByQrcodeOrUdi(String qrcode, String udi);
    
    @Query(value = "SELECT id AS id, udi AS udi, qrcode AS qrcode, cost AS cost, status AS status, usedCount AS usedCount, createTime AS createTime, usedTime AS usedTime FROM DeviceBox WHERE qrcode = :qrcode ")
    public Map<String, Object> findDeviceBoxDetailInfoByCode(@Param("qrcode") String qrcode);
    
    @Query(value = "SELECT SUM(CASE WHEN db.status IN(0,1,2,3,4,6,7) THEN 1 ELSE 0 END) AS totalQty, SUM(CASE WHEN db.status = 9 THEN 1 ELSE 0 END) AS scrappedQty, " + 
        "SUM(CASE WHEN db.status = 0 THEN 1 ELSE 0 END) AS unusedQty, SUM(CASE WHEN db.status = 4 THEN 1 ELSE 0 END) AS repairQty, " + 
        "SUM(CASE WHEN db.status IN(1,2,3) THEN 1 ELSE 0 END) AS usedQty " + 
        "FROM DeviceBox db " )
    public Map<String,Object> countNumbers();

    public Page<DeviceBox> findAll(Specification<DeviceBox> specification, Pageable page);
}
