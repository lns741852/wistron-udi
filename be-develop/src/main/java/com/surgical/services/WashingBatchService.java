package com.surgical.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surgical.entities.Package;
import com.surgical.entities.TrackingRecord;
import com.surgical.entities.WashingBatch;
import com.surgical.entities.WashingBatchDetail;
import com.surgical.enums.PackageStatus;
import com.surgical.enums.SterilizedBatchStatus;
import com.surgical.exception.AppException;
import com.surgical.repositories.PackageRepository;
import com.surgical.repositories.TrackingRecordRepository;
import com.surgical.repositories.WashingBatchDetailRepository;
import com.surgical.repositories.WashingBatchRepository;
import com.surgical.vo.WashingBatchCreateRequest;
import com.surgical.vo.WashingBatchListDto;
import com.surgical.vo.WashingBatchListVo;

@Service
public class WashingBatchService{
    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private WashingBatchRepository washingBatchRepository;

    @Autowired
    private WashingBatchDetailRepository washingBatchDetailRepository;

    @Autowired
    private TrackingRecordRepository trackingRecordRepository;
    
    @Transactional(rollbackFor = Exception.class)
    public void create(WashingBatchCreateRequest request, Long accountId){
        List<Package> packages = packageRepository.findByIdIn(request.getPackages());
        if (packages.size() != request.getPackages().size()){
            throw new AppException("輸入包盤有誤");
        }
        Date now = new Date();
        WashingBatch washingBatch = new WashingBatch();
        washingBatch.setName(request.getBatchName());
        washingBatch.setWashingMachine(request.getWashingMachine());
        washingBatch.setStartTime(request.getStartTime());
        washingBatch.setCreateTime(now);
        washingBatch.setCreator(accountId);
        washingBatchRepository.save(washingBatch);
        List<TrackingRecord> trackingRecords = new ArrayList<TrackingRecord>();
        List<WashingBatchDetail> washingBatchDetails = new ArrayList<WashingBatchDetail>();
        for(Package packageEntity : packages){
            if (!packageEntity.getStatus().equals(PackageStatus.CIRCULATION.getValue()) && !packageEntity.getStatus().equals(PackageStatus.WASH_FAIL.getValue())){
                throw new AppException(packageEntity.getDeviceBoxQrcode() + " 此包盤狀態不符");
            }
            packageEntity.setStatus(PackageStatus.CIRCU_TO_PACK.getValue());
            TrackingRecord trackingRecord = new TrackingRecord(packageEntity.getTrackingId(), PackageStatus.CIRCU_TO_PACK.getValue(), now, accountId);
            trackingRecords.add(trackingRecord);
            WashingBatchDetail washingBatchDetail = new WashingBatchDetail();
            washingBatchDetail.setPackageId(packageEntity.getId());
            washingBatchDetail.setWashingBatchId(washingBatch.getId());
            washingBatchDetail.setTrackingId(packageEntity.getTrackingId());
            washingBatchDetail.setCreateTime(now);
            washingBatchDetail.setCreator(accountId);
            washingBatchDetails.add(washingBatchDetail);
        }
        trackingRecordRepository.saveAll(trackingRecords);
        washingBatchDetailRepository.saveAll(washingBatchDetails);
    }
    
    public Page<WashingBatchListVo> getWashingBatchList(WashingBatchListDto washingBatchDto, Pageable page){
        
        Page<WashingBatch> washingBatchs = washingBatchRepository.findAll(new Specification<WashingBatch>(){

            @SuppressWarnings("unchecked")
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder criteriaBuilder){
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (washingBatchDto.getType() != null){
                   switch(washingBatchDto.getType()){
                    case 0: // 未清洗
                        predicates.add(criteriaBuilder.greaterThan(root.<Date>get("startTime"), new Date()));
                        break;
                    case 1: // 清洗中
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.<Date>get("startTime"), new Date()));
                        predicates.add(criteriaBuilder.isNull(root.<Date>get("finishTime")));
                        break;
                    case 2: // 清洗完成
                        predicates.add(criteriaBuilder.isNotNull(root.<Date>get("finishTime")));
                        break;
                   }  
                }
                if(StringUtils.isNotBlank(washingBatchDto.getName())) {
                    predicates.add(criteriaBuilder.like(root.get("name"), "%"+washingBatchDto.getName()+"%"));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
            
        }, page);
        
        Page<WashingBatchListVo> result = washingBatchs.map(new Function<WashingBatch, WashingBatchListVo>(){

            @Override
            public WashingBatchListVo apply(WashingBatch washingBatch){
                WashingBatchListVo washingBatchListVo = WashingBatchListVo.builder()
                    .id(washingBatch.getId())
                    .name(washingBatch.getName())
                    .washingMachine(washingBatch.getWashingMachine())
                    .status(washingBatch.getStatus())
                    .qty(washingBatch.getWashingBatchDetails().size())
                    .createTime(washingBatch.getCreateTime())
                    .startTime(washingBatch.getStartTime())
                    .finishTime(washingBatch.getFinishTime()).build();
                return washingBatchListVo;
            }
            
        });
        
        return result;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void finish(Long id, Boolean isSuccess, Long accountId){
        Optional<WashingBatch> washingBatchOptional = washingBatchRepository.findById(id);
        if (!washingBatchOptional.isPresent()){
            throw new AppException("此清洗鍋次不存在");
        }
        WashingBatch washingBatch = washingBatchOptional.get();
        if (washingBatch.getFinishTime() != null || washingBatch.getStatus() != null){
            throw new AppException("此清洗鍋次狀態已結束, 不可變更");
        }
        Date nowDate = new Date();
        List<Long> detailPackageIds = new ArrayList<Long>();
        washingBatch.getWashingBatchDetails().forEach(detail -> {
            detailPackageIds.add(detail.getPackageId());
        });
        List<Package> packages = packageRepository.findByStatusAndIdIn(PackageStatus.CIRCU_TO_PACK.getValue(), detailPackageIds);
        if (detailPackageIds.size() != packages.size()){
            throw new AppException("鍋次中的包盤狀態異常");
        }
        washingBatch.setFinishTime(nowDate);
        List<TrackingRecord> trackingRecords = new ArrayList<TrackingRecord>();
        washingBatch.setStatus(isSuccess ? SterilizedBatchStatus.SUCCESS.getValue() : SterilizedBatchStatus.FAIL.getValue());
        for(Package pack : packages){
            pack.setStatus(isSuccess ? PackageStatus.RECV_FROM_CIRCU_OR_STOCK.getValue() : PackageStatus.WASH_FAIL.getValue());
            TrackingRecord trackingRecord = new TrackingRecord();
            trackingRecord.setStatus(isSuccess ? PackageStatus.RECV_FROM_CIRCU_OR_STOCK.getValue() : PackageStatus.WASH_FAIL.getValue());
            trackingRecord.setTrackingId(pack.getTrackingId());
            trackingRecord.setCreateTime(nowDate);
            trackingRecord.setCreator(accountId);
            trackingRecords.add(trackingRecord);
        }
        trackingRecordRepository.saveAll(trackingRecords);
        washingBatchRepository.save(washingBatch);
        packageRepository.saveAll(packages);
    }
}
