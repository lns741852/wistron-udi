package com.surgical.repositories;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.surgical.entities.DeviceImage;
import com.surgical.entities.DeviceImageId;


public interface DeviceImageRepository extends JpaRepository<DeviceImage, DeviceImageId>{
    
    @Query(value ="SELECT di.fileId AS id, di.isMain AS isMain, fr.localPath AS path "
        + "FROM DeviceImage di LEFT JOIN FileResource fr On fr.id = di.fileId "
        + "WHERE di.typeId = :typeId")
    public List<Map<String, Object>> queryForImagesByTypeId(@Param("typeId") Long typeId);
    
    public List<DeviceImage> findByFileIdIn(Set<Long> fileId);
}
