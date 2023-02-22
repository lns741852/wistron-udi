package com.surgical.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.Package;
import com.surgical.vo.DivisionPackagesVo;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long>,JpaSpecificationExecutor<Package>{

    Long countByPackageConfigId(Long packageConfigId);
    
    public List<Package> findByStatusAndIdIn(Integer status, List<Long> packageIds);

    public List<Package> findByStatusInAndIdIn(List<Integer> statuses, List<Long> packageIds);
    
    public Optional<Package>findByDeviceBoxQrcode(String qrcode);
    
    public List<Package> findByStatus(Integer status);

    public List<Package> findByStatusIn(List<Integer> statuses);
    
    public List<Package> findByPackageConfigIdAndIdIn(Long packageConfigId, List<Long> packageIds);
    
    public Optional<Package> findByTrackingId(Long trackingId);
        
    Page<Package>findByDeviceBoxQrcode(String qrcode, Pageable page);    
    
    public List<Package> findByIdIn(List<Long> packageIds);
    
    public List<Package> findByAddId(Long addId);
    
    public List<Package> findByAddIdAndStatus(Long addId, Integer status);
    
    public List<Package> findBySerialNoAndStatusNot(String serialNo, Integer status);
    
    public List<Package> findBySerialNoAndStatusNotAndIdNotIn(String serialNos, Integer Status, List<Long> ids);

    @Query("SELECT DISTINCT p.position FROM Package p WHERE p.position is not null")
    List<String> findDistinctPosition();
    
    public List<Package> findByAddIdAndStatusAndIdIn(Long addId, Integer status, List<Long> packageIds);
    
    public List<Package> findByPackageConfigIdAndStatusNotIn(Long packageConfigId, List<Integer> statuses);
    
    public Long countByStatusIn(List<Integer> statuses);
    
    @Query("SELECT NEW com.surgical.vo.DivisionPackagesVo(pc.divisionId AS divisionId, COUNT(DISTINCT p.packageConfigId) AS configQty, COUNT(p.packageConfigId) AS packageQty) "
            + "FROM Package p LEFT JOIN PackageConfig pc ON pc.id = p.packageConfigId "
            + "WHERE p.status NOT IN :statuses "
            + "GROUP BY pc.divisionId "
            + "ORDER BY pc.divisionId ASC")
    public List<DivisionPackagesVo> getDivisionPackagesByStatusNotIn(@Param("statuses") List<Integer> statuses);
    
    @Query("SELECT COUNT(*) "
            + "FROM Package p LEFT JOIN Tracking t ON t.id = p.trackingId "
            + "WHERE p.status = 6 "
            + "AND t.expireTime IS NOT NULL "
            + "AND t.expireTime <= NOW()")
    public Long countExpiredQty();
    
    @Query("SELECT COUNT(*) "
            + "FROM Package p LEFT JOIN Tracking t ON t.id = p.trackingId "
            + "WHERE p.status = 6 "
            + "AND t.expireTime IS NOT NULL "
            + "AND t.expireTime > NOW()")
    public Long countAvailableQty();
    
}
