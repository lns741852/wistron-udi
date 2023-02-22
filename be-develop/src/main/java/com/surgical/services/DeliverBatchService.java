package com.surgical.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surgical.entities.DeliverBatch;
import com.surgical.entities.DeliverBatchDetail;
import com.surgical.entities.Package;
import com.surgical.entities.PackageConfig;
import com.surgical.entities.PackageConfigDetail;
import com.surgical.entities.TrackingRecord;
import com.surgical.enums.ComponentStatusFlag;
import com.surgical.enums.DeliverBatchStatus;
import com.surgical.enums.PackageStatus;
import com.surgical.enums.StationTypeEnum;
import com.surgical.exception.AppException;
import com.surgical.repositories.DeliverBatchDetailRepository;
import com.surgical.repositories.DeliverBatchRepository;
import com.surgical.repositories.PackageRepository;
import com.surgical.repositories.TrackingRecordRepository;
import com.surgical.vo.DeliverBatchDeliveryRequest;
import com.surgical.vo.DeliverBatchDetailQuantityVo;
import com.surgical.vo.DeliverBatchListInfoVo;
import com.surgical.vo.DeliverBatchPackageListInfoVo;
import com.surgical.vo.DeliverBatchReceiveRequest;

@Service
public class DeliverBatchService{

    @Autowired
    private DeliverBatchRepository deliverBatchRepository;

    @Autowired
    private DeliverBatchDetailRepository deliverBatchDetailRepository;
    
    @Autowired
    private PackageRepository packageRepository;
    
    @Autowired
    private TrackingRecordRepository trackingRecordRepository;


    @Transactional(rollbackFor = Exception.class)
    public void delivery(DeliverBatchDeliveryRequest request, Long accountId){
        int checkPackageStatus;
        int updatePackageStatus;
        
        Long stationFrom = request.getFrom();
        Long stationTo = request.getTo();
        if (stationFrom.equals(StationTypeEnum.PACKING.getId()) && stationTo.equals(StationTypeEnum.STERILIZATION.getId())){
            checkPackageStatus = PackageStatus.PACK_DONE.getValue();
            updatePackageStatus = PackageStatus.PACK_TO_STER.getValue();
        }else if (stationFrom.equals(StationTypeEnum.STERILIZATION.getId()) && stationTo.equals(StationTypeEnum.SUPPLY.getId())){
            checkPackageStatus = PackageStatus.STER_DONE.getValue();
            updatePackageStatus = PackageStatus.STER_TO_STOCK.getValue();
        }else if (stationFrom.equals(StationTypeEnum.SUPPLY.getId()) && stationTo.equals(StationTypeEnum.PACKING.getId())){
            checkPackageStatus = PackageStatus.IN_STOCK.getValue();
            updatePackageStatus = PackageStatus.RE_STERILIZE.getValue();
        }else{
            throw new AppException("非預期流程");
        }
        List<Package> packages = packageRepository.findByStatusAndIdIn(checkPackageStatus, request.getPackages());
        
        if (packages.size() != request.getPackages().size()){
            throw new AppException("包盤狀態有誤");
        }
        Date now = new Date();
        DeliverBatch deliverBatch = new DeliverBatch();
        deliverBatch.setTitle(request.getBatchTitle());
        deliverBatch.setStatus(DeliverBatchStatus.DELIVERING.getValue());
        deliverBatch.setDeliverFrom(stationFrom);
        deliverBatch.setDeliverTo(stationTo);
        deliverBatch.setCreateTime(now);
        deliverBatchRepository.save(deliverBatch);
        List<TrackingRecord> trackingRecords = new ArrayList<TrackingRecord>();
        List<DeliverBatchDetail> deliverBatchDetails = new ArrayList<DeliverBatchDetail>();
        for(Package pack : packages){
            pack.setStatus(updatePackageStatus);
            TrackingRecord treckingRecord = new TrackingRecord(pack.getTrackingId(), updatePackageStatus, now, accountId);
            trackingRecords.add(treckingRecord);
            DeliverBatchDetail deliverBatchDetail = new DeliverBatchDetail();
            deliverBatchDetail.setPackageId(pack.getId());
            deliverBatchDetail.setTrackingId(pack.getTrackingId());
            deliverBatchDetail.setDeliverBatchId(deliverBatch.getId());
            deliverBatchDetail.setCreateTime(now);
            deliverBatchDetails.add(deliverBatchDetail);
        }
        packageRepository.saveAll(packages);
        trackingRecordRepository.saveAll(trackingRecords);
        deliverBatchDetailRepository.saveAll(deliverBatchDetails);
    }

    public Page<DeliverBatchListInfoVo> list(Long stationId, Pageable pageable){
        Page<DeliverBatch> deliverBatchs = deliverBatchRepository.findByDeliverToAndStatus(stationId, DeliverBatchStatus.DELIVERING.getValue(), pageable);
        Page<DeliverBatchListInfoVo> deliverBatchListInfos = deliverBatchs.map(new Function<DeliverBatch, DeliverBatchListInfoVo>(){

            @Override
            public DeliverBatchListInfoVo apply(DeliverBatch deliverBatch){
                DeliverBatchListInfoVo deliverBatchList = new DeliverBatchListInfoVo();
                deliverBatchList.setId(deliverBatch.getId());
                deliverBatchList.setTitle(deliverBatch.getTitle());
                deliverBatchList.setStatus(deliverBatch.getStatus());
                deliverBatchList.setFromStationId(deliverBatch.getDeliverFrom());
                deliverBatchList.setToStationId(deliverBatch.getDeliverTo());
                deliverBatchList.setCreateTime(deliverBatch.getCreateTime());
                deliverBatchList.setTotalQty(0L);
                deliverBatchList.setWaitForReceiveQty(0L);
                return deliverBatchList;
            }
        });
        Map<Long, DeliverBatchDetailQuantityVo> deliverBatchDetailQuantityInfo = getDeliverBatchDetailQuantityInfo(deliverBatchListInfos.getContent());
        if (!deliverBatchDetailQuantityInfo.isEmpty()){
            for(DeliverBatchListInfoVo deliverBatchListInfo : deliverBatchListInfos){
                deliverBatchListInfo.setTotalQty(deliverBatchDetailQuantityInfo.get(deliverBatchListInfo.getId()).getTotalQty());
                deliverBatchListInfo.setWaitForReceiveQty(deliverBatchDetailQuantityInfo.get(deliverBatchListInfo.getId()).getWaitForReceiveQty());
            }
        }
        return deliverBatchListInfos;
    }
    
    private Map<Long, DeliverBatchDetailQuantityVo> getDeliverBatchDetailQuantityInfo(List<DeliverBatchListInfoVo> deliverBatchListInfos){
        Map<Long, DeliverBatchDetailQuantityVo> deliverBatchDetailQuantityInfo = new HashMap<Long, DeliverBatchDetailQuantityVo>();
        List<Long> deliverBatchIds = new ArrayList<Long>();
        for(DeliverBatchListInfoVo deliverBatchList : deliverBatchListInfos){
            deliverBatchIds.add(deliverBatchList.getId());
            deliverBatchDetailQuantityInfo.put(deliverBatchList.getId(), new DeliverBatchDetailQuantityVo());
        }
        List<DeliverBatchDetail> deliverBatchDetails = deliverBatchDetailRepository.findByDeliverBatchIdIn(deliverBatchIds);
        if (!deliverBatchDetails.isEmpty()){
            for(DeliverBatchDetail deliverBatchDetail : deliverBatchDetails){
                DeliverBatchDetailQuantityVo deliverBatchDetailQuantity = deliverBatchDetailQuantityInfo.get(deliverBatchDetail.getDeliverBatchId());
                deliverBatchDetailQuantity.setTotalQty(deliverBatchDetailQuantity.getTotalQty() + 1);
                if (null == deliverBatchDetail.getReceiveTime()){
                    deliverBatchDetailQuantity.setWaitForReceiveQty(deliverBatchDetailQuantity.getWaitForReceiveQty() + 1);
                }
            }
        }
        return deliverBatchDetailQuantityInfo;
    }
    
    public List<DeliverBatchPackageListInfoVo> packageList(Long deliverBatchId){
        List<DeliverBatchPackageListInfoVo> deliverBatchPackageListInfos = new ArrayList<DeliverBatchPackageListInfoVo>();
        List<DeliverBatchDetail> deliverBatchDetails = deliverBatchDetailRepository.findByDeliverBatchId(deliverBatchId);
        if (!deliverBatchDetails.isEmpty()){
            for(DeliverBatchDetail deliverBatchDetail : deliverBatchDetails){
                Package pkg = deliverBatchDetail.getPkg();
                if (pkg == null) continue;
                PackageConfig packageConfig = pkg.getPackageConfig();
                if (packageConfig == null) continue;
                List<PackageConfigDetail> packageConfigDetails = packageConfig.getPackageConfigDetails();
                if (packageConfigDetails.isEmpty()) continue;
                DeliverBatchPackageListInfoVo deliverBatchPackageListInfo = new DeliverBatchPackageListInfoVo();
                deliverBatchPackageListInfo.setId(pkg.getId());
                deliverBatchPackageListInfo.setQrcode(pkg.getDeviceBoxQrcode());
                deliverBatchPackageListInfo.setPackageCode(pkg.getCode());
                deliverBatchPackageListInfo.setConfigCode(packageConfig.getConfigCode());
                deliverBatchPackageListInfo.setSerialNo(pkg.getSerialNo());
                deliverBatchPackageListInfo.setPosition(pkg.getPosition());
                deliverBatchPackageListInfo.setTrackingId(pkg.getTrackingId());
                deliverBatchPackageListInfo.setConfigId(packageConfig.getId());
                deliverBatchPackageListInfo.setConfigName(packageConfig.getPackageName());
                deliverBatchPackageListInfo.setDivisionId(packageConfig.getDivisionId());
                deliverBatchPackageListInfo.setStatus(pkg.getStatus());
                deliverBatchPackageListInfo.setReceiveTime(deliverBatchDetail.getReceiveTime());
                deliverBatchPackageListInfos.add(deliverBatchPackageListInfo);
            }
        }
        return deliverBatchPackageListInfos;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void receive(DeliverBatchReceiveRequest request, Long accountId){
        Optional<DeliverBatch> deliverBatchOptional = deliverBatchRepository.findById(request.getDeliverBatchId());
        if (!deliverBatchOptional.isPresent()){
            throw new AppException("查無此批次");
        }
        DeliverBatch deliverBatch = deliverBatchOptional.get();
        if (deliverBatch.getDeliverTo() != request.getStationId()){
            throw new AppException("此批次非送達此供應站");
        }
        Long stationId = request.getStationId();
        int checkPackageStatus;
        int updatePackageStatus;
        if (stationId.equals(StationTypeEnum.STERILIZATION.getId())){
            checkPackageStatus = PackageStatus.PACK_TO_STER.getValue();
            updatePackageStatus = PackageStatus.STER_RECV.getValue();
        }else if (stationId.equals(StationTypeEnum.SUPPLY.getId())){
            checkPackageStatus = PackageStatus.STER_TO_STOCK.getValue();
            updatePackageStatus = PackageStatus.IN_STOCK.getValue();
        }else if (stationId.equals(StationTypeEnum.PACKING.getId())){
            checkPackageStatus = PackageStatus.RE_STERILIZE.getValue();
            updatePackageStatus = PackageStatus.RECV_FROM_EXPIRED_STER.getValue();
        }else{
            throw new AppException("非預期流程");
        }
        //判斷傳入的packageIds 有沒有不在此批的
        List<Package> packages = packageRepository.findByStatusAndIdIn(checkPackageStatus, request.getPackages());
        if (packages.size() != request.getPackages().size()){
            throw new AppException("包盤狀態異常");
        }
        List<DeliverBatchDetail> unreceivedDetails = deliverBatch.getDeliverBatchDetails().stream().filter(d -> d.getReceiveTime() == null).collect(Collectors.toList());
        List<Long> unreceivedPackageIds = unreceivedDetails.stream().map(DeliverBatchDetail::getPackageId).collect(Collectors.toList());
        if (!unreceivedPackageIds.containsAll(request.getPackages())){
            throw new AppException("傳入包盤異常，不屬於此批次內容");
        }
        Date now = new Date();
        List<TrackingRecord> trackingRecords = new ArrayList<TrackingRecord>();
        for(Package pack : packages){
            pack.setStatus(updatePackageStatus);
            TrackingRecord trackingRecord = new TrackingRecord(pack.getTrackingId(), updatePackageStatus, now, accountId);
            trackingRecords.add(trackingRecord);
            
            pack.getDeviceBox().setStatus(ComponentStatusFlag.REPLACED.getValue());
            pack.getDevice().forEach(d -> {
                d.setStatus(ComponentStatusFlag.RECEIVE_SCAN_DONE.getValue());
            });
        }
        for(DeliverBatchDetail deliverBatchDetail : unreceivedDetails){
            if (!request.getPackages().contains(deliverBatchDetail.getPackageId())){
                continue;
            }
            deliverBatchDetail.setReceiveTime(now);
        }
        if (unreceivedDetails.size() == request.getPackages().size()){
            deliverBatch.setStatus(DeliverBatchStatus.COMPLETE.getValue());
            deliverBatchRepository.save(deliverBatch);
        }
        trackingRecordRepository.saveAll(trackingRecords);
        packageRepository.saveAll(packages);
        deliverBatchDetailRepository.saveAll(unreceivedDetails);
    }
}