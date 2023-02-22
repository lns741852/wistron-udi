package com.surgical.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surgical.entities.Device;
import com.surgical.entities.DeviceImage;
import com.surgical.entities.DeviceType;
import com.surgical.entities.FileResource;
import com.surgical.exception.AppException;
import com.surgical.repositories.DeviceImageRepository;
import com.surgical.repositories.DeviceRepository;
import com.surgical.repositories.DeviceTypeRepository;
import com.surgical.repositories.FileResourceRepository;
import com.surgical.vo.DeviceTypeAddRequest;
import com.surgical.vo.DeviceTypeDetail;
import com.surgical.vo.DeviceTypeImagesVo;
import com.surgical.vo.DeviceTypeList;


@Service
public class DeviceTypeService{

    @Autowired
    DeviceTypeRepository deviceTypeRepository;

    @Autowired
    FileResourceRepository fileResourceRepository;
    
    @Autowired
    DeviceImageRepository deviceImageRepository;
    
    @Autowired
    DeviceRepository deviceRepository;

    @PersistenceContext
    private EntityManager em;
    
    @Transactional(rollbackFor = Exception.class)
    public Long addDeviceType(DeviceTypeAddRequest request, Long accountId){
        Optional<DeviceType> existOptional = deviceTypeRepository.findByNameScientificAndSpec(request.getNameScientific(), request.getSpec());
        if (!existOptional.isPresent()){
            DeviceType deviceType = setDeviceType(new DeviceType(), request, accountId, new Date());
            return deviceType.getId();
        }else{
            throw new AppException("duplicate type");
        }
    }

    private DeviceType setDeviceType(DeviceType deviceType, DeviceTypeAddRequest request, Long accountId, Date now){
        //判斷是否有主圖
        if (request.getImages().stream().filter(d -> d.getIsMain() == true).collect(Collectors.toList()).size() != 1){
            throw new AppException("主圖設定有問題");
        }
        Set<FileResource> fileResourceList = fileResourceRepository.findByIdIn(request.getImages().stream().map(d -> d.getId()).collect(Collectors.toSet()));
        if (fileResourceList.size() != request.getImages().size()){
            throw new AppException("圖片選擇有誤");
        }
        deviceType.setName(request.getName());
        deviceType.setNameScientific(request.getNameScientific());
        deviceType.setSpec(request.getSpec());
        deviceType.setDescription(request.getDesc());
        deviceType.setUpdater(accountId);
        deviceType.setUpdateTime(now);
        if (request.getId() == null){
            deviceType.setCreateTime(now);
        }
        deviceType = deviceTypeRepository.save(deviceType);
        doImages(deviceType, request);
        return deviceType;
    }

    private void doImages(DeviceType deviceType, DeviceTypeAddRequest request){
        //圖片的mapping
        Set<DeviceImage> deviceImages = new HashSet<DeviceImage>();
        for(DeviceTypeImagesVo deviceImageVo : request.getImages()){
            DeviceImage deviceImage = new DeviceImage();
            deviceImage.setFileId(deviceImageVo.getId());
            deviceImage.setTypeId(deviceType.getId());
            deviceImage.setIsMain(deviceImageVo.getIsMain());
            deviceImages.add(deviceImage);
        }
        deviceImageRepository.saveAll(deviceImages);
    }

    public DeviceTypeDetail detail(Long typeId){
        Optional<DeviceType> deviceTypeOptional = deviceTypeRepository.findById(typeId);
        if (!deviceTypeOptional.isPresent()) {
            throw new AppException("此器械類型部不存在 ");
        }
        DeviceTypeDetail result = new DeviceTypeDetail();
        DeviceType deviceType = deviceTypeOptional.get();
        result.setId(deviceType.getId());
        result.setName(deviceType.getName());
        result.setSpec(deviceType.getSpec());
        result.setDesc(deviceType.getDescription());
        result.setNameScientific(deviceType.getNameScientific());
        result.setImages(deviceImageRepository.queryForImagesByTypeId(typeId));
        result.setDivision(deviceTypeRepository.queryDeviceQuantityByTypeId(typeId));
        return result;
    }
    
    public Page<DeviceTypeList> getDeviceTypeList( Long divisionId, String nameScientific, String spec, Pageable page){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DeviceTypeList> cq = cb.createQuery(DeviceTypeList.class);
        Root<DeviceType> root = cq.from(DeviceType.class);
        Join<DeviceType, Device> deviceJoin = root.join("devices", JoinType.LEFT);
        Join<DeviceType, DeviceImage> deviceImageJoin = root.join("deviceImages", JoinType.LEFT);
        Join<DeviceImage, FileResource>fileResouseJoin = deviceImageJoin.join("fileResource",JoinType.INNER);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(deviceImageJoin.get("isMain"),true));
        if (divisionId != null){
            predicates.add(cb.equal(deviceJoin.get("division"), divisionId));
        }
        if (StringUtils.isNotBlank(nameScientific)){
            predicates.add(cb.or(cb.like(root.get("name"), "%" + nameScientific + "%"), 
                                 cb.like(root.get("nameScientific"), "%" + nameScientific + "%")
                                 ));
        }
        if (StringUtils.isNotBlank(spec)){
            predicates.add(cb.like(root.get("spec"), "%" + spec + "%"));
        }
        cq.where(predicates.toArray(new Predicate[predicates.size()]));
        cq.multiselect(root.get("id"),
                       root.get("name"),
                       root.get("spec"),
                       root.get("description"),
                       root.get("nameScientific"),
                       cb.sum(cb.<Number>selectCase()
                              .when(cb.equal(deviceJoin.get("status"), 0), 1)
                              .when(cb.equal(deviceJoin.get("status"), 1), 1)
                              .when(cb.equal(deviceJoin.get("status"), 2), 1)
                              .when(cb.equal(deviceJoin.get("status"), 3), 1)
                              .when(cb.equal(deviceJoin.get("status"), 4), 1)
                              .when(cb.equal(deviceJoin.get("status"), 6), 1)
                              .when(cb.equal(deviceJoin.get("status"), 7), 1)
                              .otherwise(0)),
                       fileResouseJoin.get("localPath")
                       );
        cq.groupBy(root.get("id"),
                   root.get("name"),
                   root.get("spec"),
                   root.get("description"),
                   root.get("nameScientific"),
                   fileResouseJoin.get("localPath")
                   );
        List<Order> orderList = new ArrayList<Order>();
        orderList.add(cb.asc(root.get("nameScientific")));
        orderList.add(cb.asc(root.get("spec")));
        cq.orderBy(orderList);
        TypedQuery<DeviceTypeList> qry = em.createQuery(cq);
        List<DeviceTypeList> deviceTypeVOList = qry.getResultList();
        Page<DeviceTypeList> deviceTypesPageable = listConvertToPage(deviceTypeVOList, page);
        return deviceTypesPageable;
    }
    
    public <T> Page<T> listConvertToPage(List<T> list, Pageable page){
        int start = (int) page.getOffset();
        int end = ( start + page.getPageSize() ) > list.size() ? list.size() : ( start + page.getPageSize() );
        if(start>end) {
            start=end;     
        }
        return new PageImpl<T>(list.subList(start, end), page, list.size());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateDeviceType(DeviceTypeAddRequest request, Long accountId){
        Optional<DeviceType> deviceTypeOptional = deviceTypeRepository.findById(request.getId());
        Optional<DeviceType> specExistOptional = deviceTypeRepository.findByNameScientificAndSpecAndIdIsNot(request.getNameScientific(), request.getSpec(), request.getId());
        if (!deviceTypeOptional.isPresent()) {
            throw new AppException("此種類不存在");
        }
        if (specExistOptional.isPresent()) {
            throw new AppException("此種器械類型已存在");
        }
        setDeviceType(deviceTypeOptional.get(), request, accountId, new Date());
    }
    
    public List<Map<String, Object>> deviceModelList(Long typeId, boolean info, Long divisionId){
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (info == true){
            result = deviceRepository.getModelListByTypeIdWithInfo(typeId, divisionId);
        }else{
            result = deviceRepository.getModelListByTypeIdWithoutInfo(typeId);
        }
        return result;
    }
}
