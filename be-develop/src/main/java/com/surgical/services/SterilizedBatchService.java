package com.surgical.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surgical.entities.Package;
import com.surgical.entities.SterilizedBatch;
import com.surgical.entities.SterilizedBatchDetail;
import com.surgical.entities.SterilizedMonitor;
import com.surgical.entities.Tracking;
import com.surgical.entities.TrackingRecord;
import com.surgical.enums.PackageStatus;
import com.surgical.enums.SterilizedBatchStatus;
import com.surgical.enums.SterilizedBatchType;
import com.surgical.exception.AppException;
import com.surgical.repositories.PackageRepository;
import com.surgical.repositories.SterilizedBatchDetailRepository;
import com.surgical.repositories.SterilizedBatchRepository;
import com.surgical.repositories.SterilizedMonitorRepository;
import com.surgical.repositories.TrackingRecordRepository;
import com.surgical.repositories.TrackingRepository;
import com.surgical.vo.MonitorItem;
import com.surgical.vo.SterilizedBatchCreateRequest;
import com.surgical.vo.SterilizedBatchDetailResponseVo;
import com.surgical.vo.SterilizedBatchFinishMonitorItem;
import com.surgical.vo.SterilizedBatchFinishRequest;
import com.surgical.vo.SterilizedBatchPackageResponse;
import com.surgical.vo.SterilizedBatchVo;

@Service
public class SterilizedBatchService{

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private SterilizedBatchRepository sterilizedBatchRepository;

    @Autowired
    private TrackingRecordRepository trackingRecordRepository;

    @Autowired
    private SterilizedBatchDetailRepository sterilizedBatchDetailRepository;

    @Autowired
    private TrackingRepository trackingRepository;
    
    @Autowired
    private SterilizedMonitorRepository sterilizedMonitorRepository;

    @Transactional(rollbackFor = Exception.class)
    public void create(SterilizedBatchCreateRequest request, Long accountId){
        List<Package> packages = packageRepository.findByStatusInAndIdIn(Arrays.asList(PackageStatus.STER_RECV.getValue(), PackageStatus.STER_FAIL.getValue()), request.getPackages());
        if (packages.size() != request.getPackages().size()){
            throw new AppException("包盤狀態有誤");
        }
        Date now = new Date();
        SterilizedBatch sterilizedBatch = new SterilizedBatch();
        sterilizedBatch.setName(request.getBatchName());
        sterilizedBatch.setSterilizer(request.getSterilizer());
        sterilizedBatch.setStartTime(request.getStartTime());
        sterilizedBatch.setCreateTime(now);
        sterilizedBatch.setCreator(accountId);
        sterilizedBatch.setPetriDish(request.getPetriDish());
        sterilizedBatchRepository.save(sterilizedBatch);
        List<TrackingRecord> trackingRecords = new ArrayList<TrackingRecord>();
        List<SterilizedBatchDetail> sterilizedBatchDetails = new ArrayList<SterilizedBatchDetail>();
        for(Package pack : packages){
            pack.setStatus(PackageStatus.STER_PROCESS.getValue());
            TrackingRecord trackingRecord = new TrackingRecord(pack.getTrackingId(), PackageStatus.STER_PROCESS.getValue(), now, accountId);
            trackingRecords.add(trackingRecord);
            SterilizedBatchDetail sterilizedBatchDetail = new SterilizedBatchDetail();
            sterilizedBatchDetail.setPackageId(pack.getId());
            sterilizedBatchDetail.setSterilizedBatchId(sterilizedBatch.getId());
            sterilizedBatchDetail.setTrackingId(pack.getTrackingId());
            sterilizedBatchDetail.setCreateTime(now);
            sterilizedBatchDetail.setCreator(accountId);
            sterilizedBatchDetails.add(sterilizedBatchDetail);
        }
        packageRepository.saveAll(packages);
        trackingRecordRepository.saveAll(trackingRecords);
        sterilizedBatchDetailRepository.saveAll(sterilizedBatchDetails);
    }

    @Transactional(rollbackFor = Exception.class)
    public void finish(SterilizedBatchFinishRequest request, Long accountId){
        Optional<SterilizedBatch> sterilizedBatchOptional = sterilizedBatchRepository.findById(request.getId());
        if (!sterilizedBatchOptional.isPresent()){
            throw new AppException("此滅菌鍋次不存在");
        }
        if (request.getIsSuccess() && null == request.getExpireTime()) {
            throw new AppException("ExpireTime cannot be null if isSuccess is true");
        }
        SterilizedBatch sterilizedBatch = sterilizedBatchOptional.get();
        if (sterilizedBatch.getFinishTime() != null || sterilizedBatch.getStatus() != null){
            throw new AppException("此滅菌鍋次狀態不可完成");
        }
        if (sterilizedBatch.getStartTime().after(request.getFinishTime())) {
            throw new AppException("完成滅菌時間不可小於滅菌起始時間");
        }
        Date now = new Date();
        sterilizedBatch.setFinishTime(request.getFinishTime());
        sterilizedBatch.setStatus((request.getIsSuccess())? SterilizedBatchStatus.SUCCESS.getValue() : SterilizedBatchStatus.FAIL.getValue());
        List<Long> detailPackageIds = new ArrayList<Long>();
        if (!request.getIsSuccess()){
            List<SterilizedBatchDetail> sterilizedBatchDetails = new ArrayList<SterilizedBatchDetail>();
            sterilizedBatch.getSterilizedBatchDetails().forEach(detail -> {
                detail.setStatus(SterilizedBatchStatus.FAIL.getValue());
                sterilizedBatchDetails.add(detail);
                detailPackageIds.add(detail.getPackageId());
            });
            sterilizedBatchDetailRepository.saveAll(sterilizedBatchDetails);
        }else{
            sterilizedBatch.getSterilizedBatchDetails().forEach(detail -> {
                detailPackageIds.add(detail.getPackageId());
            });
        }
        List<Package> packages = packageRepository.findByStatusAndIdIn(PackageStatus.STER_PROCESS.getValue(), detailPackageIds);
        if (detailPackageIds.size() != packages.size()){
            throw new AppException("鍋次中的包盤狀態異常");
        }
        List<SterilizedMonitor> monitors = new ArrayList<SterilizedMonitor>();
        if(request.getMonitorItems() != null && request.getMonitorItems().size() > 0) {
            for (SterilizedBatchFinishMonitorItem item : request.getMonitorItems()) {
                SterilizedMonitor monitor = new SterilizedMonitor();
                monitor.setSterilizedBatchId(sterilizedBatch.getId());
                monitor.setType(item.getType());
                monitor.setIndicator(item.getIndicator() == null ? null : item.getIndicator());
                monitor.setResult(item.getResult() == null ? null : item.getResult());
                monitors.add(monitor);
            }
        }

        List<TrackingRecord> trackingRecords = new ArrayList<TrackingRecord>();
        if (request.getIsSuccess()){
            List<Tracking> trackings = new ArrayList<Tracking>();
            for(Package pack : packages){
                pack.setStatus(PackageStatus.STER_DONE.getValue());
                Tracking tracking = pack.getTracking();
                tracking.setExpireTime(request.getExpireTime());
                trackings.add(tracking);
                trackingRecords.add(new TrackingRecord(tracking.getId(), PackageStatus.STER_DONE.getValue(), now, accountId));
            }
            trackingRepository.saveAll(trackings);
        }else{
            for(Package pack : packages){
                pack.setStatus(PackageStatus.STER_FAIL.getValue());
                trackingRecords.add(new TrackingRecord(pack.getTracking().getId(), PackageStatus.STER_FAIL.getValue(), now, accountId));
            }
        }        
        trackingRecordRepository.saveAll(trackingRecords);
        sterilizedBatchRepository.save(sterilizedBatch);
        packageRepository.saveAll(packages);
        sterilizedMonitorRepository.saveAll(monitors);
    }
    
    public Page<SterilizedBatchVo> getList(Integer type, String name, Pageable page){
        Specification<SterilizedBatch> specification = new Specification<SterilizedBatch>(){

            public Predicate toPredicate(Root<SterilizedBatch> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder){
                List<Predicate> predicates = new ArrayList<Predicate>();
                Date nowDate = new Date();
                if (null != type){
                    if (SterilizedBatchType.UNSTERILIZED.getValue() == type){//0:未滅菌
                        predicates.add(criteriaBuilder.greaterThan(root.get("startTime"), nowDate));
                    }else if (SterilizedBatchType.STERILIZING.getValue() == type){//1滅菌中
                        predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get("startTime"), nowDate), criteriaBuilder.isNull(root.get("finishTime"))));
                    }else if (SterilizedBatchType.COMPLETED.getValue() == type){//2:滅菌完成
                        predicates.add(criteriaBuilder.isNotNull(root.get("finishTime")));
                    }else {
                        throw new AppException("無此輸入狀態!");
                    }
                }
                if (null != name){
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        Page<SterilizedBatch> recordPage = sterilizedBatchRepository.findAll(specification, page);
        Page<SterilizedBatchVo> result = recordPage.map(new Function<SterilizedBatch, SterilizedBatchVo>(){

            @Override
            public SterilizedBatchVo apply(SterilizedBatch sterilizedBatch){
                SterilizedBatchVo sterilizedBatchVo = new SterilizedBatchVo();
                sterilizedBatchVo.setId(sterilizedBatch.getId());
                sterilizedBatchVo.setName(sterilizedBatch.getName());
                sterilizedBatchVo.setSterilizer(sterilizedBatch.getSterilizer());
                if (null != sterilizedBatch.getStatus()){
                    sterilizedBatchVo.setStatus(SterilizedBatchStatus.SUCCESS.getValue() == sterilizedBatch.getStatus() ? SterilizedBatchStatus.SUCCESS.getValue() : SterilizedBatchStatus.FAIL.getValue());
                    sterilizedBatchVo.setFinishTime(sterilizedBatch.getFinishTime());
                }
                sterilizedBatchVo.setQty(sterilizedBatch.getSterilizedBatchDetails().size());
                sterilizedBatchVo.setStartTime(sterilizedBatch.getStartTime());
                sterilizedBatchVo.setCreateTime(sterilizedBatch.getCreateTime());
                return sterilizedBatchVo;
            }
        });
        return result;
    }
    
    public SterilizedBatchDetailResponseVo detail(Long id, Long trackingId){
        if (id == null && trackingId == null){
            throw new AppException("請輸入 id 或 trackingId");
        }
        SterilizedBatch sterilizedBatch;
        SterilizedBatchDetailResponseVo sterilizedBatchDetailResponseVo = new SterilizedBatchDetailResponseVo();
        if (trackingId != null){
            List<SterilizedBatchDetail> sterilizedBatchDetails = sterilizedBatchDetailRepository.findAllByTrackingId(trackingId);
            if (sterilizedBatchDetails.isEmpty()) {
                return sterilizedBatchDetailResponseVo;
            }
            Optional<SterilizedBatchDetail> sterilizedBatchDetailOpt = sterilizedBatchDetails.stream().filter(sbd -> sbd.getSterilizedBatch().getStatus() == SterilizedBatchStatus.SUCCESS.getValue()).findAny();
            if (!sterilizedBatchDetailOpt.isPresent()) {
                return sterilizedBatchDetailResponseVo;
            }
            sterilizedBatch = sterilizedBatchDetailOpt.get().getSterilizedBatch();
        }else{
            Optional<SterilizedBatch> sterilizedBatchOpt = sterilizedBatchRepository.findById(id);
            sterilizedBatch = sterilizedBatchOpt.orElseThrow(() -> new AppException("查無此滅菌鍋次明細"));
        }
        sterilizedBatchDetailResponseVo.setId(sterilizedBatch.getId());
        sterilizedBatchDetailResponseVo.setName(sterilizedBatch.getName());
        sterilizedBatchDetailResponseVo.setSterilizer(sterilizedBatch.getSterilizer());
        sterilizedBatchDetailResponseVo.setStatus(sterilizedBatch.getStatus());
        sterilizedBatchDetailResponseVo.setQty(sterilizedBatch.getSterilizedBatchDetails().size());
        sterilizedBatchDetailResponseVo.setFinishTime(sterilizedBatch.getFinishTime());
        sterilizedBatchDetailResponseVo.setStartTime(sterilizedBatch.getStartTime());
        sterilizedBatchDetailResponseVo.setCreateTime(sterilizedBatch.getCreateTime());
        sterilizedBatchDetailResponseVo.setMonitorItems(sterilizedBatch.getSterilizedMonitors().stream().map(MonitorItem::new).collect(Collectors.toList()));
        return sterilizedBatchDetailResponseVo;
    }
    
    public List<SterilizedBatchPackageResponse> packageList(Long id){
        List<SterilizedBatchPackageResponse> result = sterilizedBatchDetailRepository.findForSterilizedBatchPackageList(id);
        if(result.isEmpty()) {
            throw new AppException("查無此批次");
        }
        return result;
    }
}
