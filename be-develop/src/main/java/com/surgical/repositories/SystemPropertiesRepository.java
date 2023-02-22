package com.surgical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.surgical.entities.SystemProperties;
import com.surgical.vo.SystemPropertiesVo;

@Repository
public interface SystemPropertiesRepository extends JpaRepository<SystemProperties, Long>{
    
    @Query(value = "SELECT s.value FROM SystemProperties s WHERE s.name = :name")
    public String findValueByName(@Param("name") String name);
    
    public boolean existsSystemPropertiesByName(String name);
    
    @Query(value = "SELECT NEW com.surgical.vo.SystemPropertiesVo(sp.id AS id, sp.name AS name, sp.value AS value, sp.description AS description, a.account AS updater, sp.updateTime AS updateTime) "
        + "FROM SystemProperties sp LEFT JOIN Admin a ON a.id = sp.updater "
        + "WHERE (sp.name LIKE CONCAT('%', COALESCE(:search, ''),'%') OR :search IS NULL ) "
        + "OR (sp.value LIKE CONCAT('%', COALESCE(:search, ''),'%') OR :search IS NULL ) "
        + "OR (sp.description LIKE CONCAT('%', COALESCE(:search, ''),'%') OR :search IS NULL ) "
        + "GROUP BY sp.id "
        + "ORDER BY sp.id ASC")
    public List<SystemPropertiesVo> findBySearch(@Param("search") String search);
}
