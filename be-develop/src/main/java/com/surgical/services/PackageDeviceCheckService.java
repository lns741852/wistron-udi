package com.surgical.services;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.surgical.entities.Device;
import com.surgical.entities.DeviceCheckDiff;
import com.surgical.entities.DeviceCheckLog;
import com.surgical.entities.DeviceType;
import com.surgical.entities.Package;
import com.surgical.entities.PackageConfig;
import com.surgical.entities.PackageConfigDetail;
import com.surgical.entities.PackageDeviceCheck;
import com.surgical.entities.PackageDeviceRecord;
import com.surgical.entities.PackageOrder;
import com.surgical.entities.PackageOrderMapping;
import com.surgical.entities.SterilizedBatchDetail;
import com.surgical.entities.SurgicalApply;
import com.surgical.entities.TrackingRecord;
import com.surgical.enums.ComponentStatusFlag;
import com.surgical.enums.PackageOrderMappingStatus;
import com.surgical.enums.PackageOrderStatus;
import com.surgical.enums.PackageStatus;
import com.surgical.enums.SurgicalApplyStatus;
import com.surgical.exception.AppException;
import com.surgical.repositories.DeviceCheckDiffRepository;
import com.surgical.repositories.DeviceCheckLogRepository;
import com.surgical.repositories.DeviceImageRepository;
import com.surgical.repositories.DeviceRepository;
import com.surgical.repositories.DeviceTypeRepository;
import com.surgical.repositories.PackageDeviceCheckRepository;
import com.surgical.repositories.PackageDeviceRecordRepository;
import com.surgical.repositories.PackageOrderMappingRepository;
import com.surgical.repositories.PackageOrderRepository;
import com.surgical.repositories.PackageRepository;
import com.surgical.repositories.SterilizedBatchDetailRepository;
import com.surgical.repositories.SurgicalApplyRepository;
import com.surgical.repositories.TrackingRecordRepository;
import com.surgical.vo.DeviceCheckGetInfoVo;
import com.surgical.vo.DeviceTypeImagesVo;
import com.surgical.vo.DeviceTypeVo;
import com.surgical.vo.PackageDeviceCheckDetailDeviceTypeResponse;
import com.surgical.vo.PackageDeviceCheckDetailResponse;
import com.surgical.vo.PackageDeviceCheckDetailStepCheckVo;
import com.surgical.vo.PackageDeviceCheckDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PackageDeviceCheckService{

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    @Autowired
    private DeviceImageRepository deviceImageRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceCheckDiffRepository deviceCheckDiffRepository;
    
    @Autowired
    private PackageDeviceCheckRepository packageDeviceCheckRepository;
    
    @Autowired
    private PackageOrderMappingRepository packageOrderMappingRepository;
    
    @Autowired
    private TrackingRecordRepository trackingRecordRepository;
    
    @Autowired
    private PackageOrderRepository packageOrderRepository;
    
    @Autowired
    private PackageDeviceRecordRepository packageDeviceRecordRepository;
    
    @Autowired
    private DeviceCheckLogRepository deviceCheckLogRepository;
    
    @Autowired
    private SurgicalApplyRepository surgicalApplyRepository;
    
    @Autowired
    private SterilizedBatchDetailRepository sterilizedBatchDetailRepository;

    public PackageDeviceCheckDetailResponse detail(Long trackingId){
        Optional<Package> packageOpt = packageRepository.findByTrackingId(trackingId);
        if (!packageOpt.isPresent()){
            throw new AppException("查無此盤包");
        }
        Package packageEntity = packageOpt.get();
        PackageConfig packageConfig = packageEntity.getPackageConfig();
        PackageDeviceCheckDetailResponse response = new PackageDeviceCheckDetailResponse();
        response.setConfigCode(packageConfig.getConfigCode());
        response.setConfigName(packageConfig.getPackageName());
        response.setDivisionId(packageConfig.getDivisionId());
        response.setPackageId(packageEntity.getId());
        response.setStatus(packageEntity.getStatus());
        response.setQrcode(packageEntity.getDeviceBoxQrcode());
        response.setTrackingId(trackingId);
        response.setPackageSerialNo(packageEntity.getSerialNo());
        List<PackageConfigDetail> packageConfigDetails = packageConfig.getPackageConfigDetails();
        List<PackageDeviceCheckDetailDeviceTypeResponse> deviceTypesForResponse = new ArrayList<PackageDeviceCheckDetailDeviceTypeResponse>();
        List<DeviceType> deviceTypes = deviceTypeRepository.getAllTypesByConfigId(packageConfig.getId());
        List<Map<String, Object>> typesStepsMap = deviceCheckDiffRepository.getAllDeviceCheckDiffsByTrackingIg(trackingId);
        for(DeviceType deviceType : deviceTypes){
            PackageDeviceCheckDetailDeviceTypeResponse deviceTypeResponse = new PackageDeviceCheckDetailDeviceTypeResponse();
            PackageConfigDetail packageConfigDetail = packageConfigDetails.stream().filter(pcd -> pcd.getDeviceTypeId().equals(deviceType.getId())).findAny().get();
            deviceTypeResponse.setTypeId(deviceType.getId());
            deviceTypeResponse.setQty(packageConfigDetail.getAmount());
            deviceTypeResponse.setNameScientific(deviceType.getNameScientific());
            deviceTypeResponse.setName(deviceType.getName());
            deviceTypeResponse.setSpec(deviceType.getSpec());
            deviceTypeResponse.setDesc(deviceType.getDescription());
            deviceTypeResponse.setImages(doMainImage(deviceType.getId()));
            List<PackageDeviceCheckDetailStepCheckVo> stepCheckVos = new ArrayList<PackageDeviceCheckDetailStepCheckVo>();
            List<Map<String, Object>> typeStepsMap = new ArrayList<Map<String, Object>>();
            if (!typesStepsMap.isEmpty()){
                typeStepsMap = typesStepsMap.stream().filter(stm -> stm.get("deviceTypeId").equals(deviceType.getId())).collect(Collectors.toList());
            }
            List<PackageDeviceCheck> checkedSteps = packageDeviceCheckRepository.findByTrackingId(trackingId);
            for(PackageDeviceCheck packageDeviceCheck : checkedSteps) {
                PackageDeviceCheckDetailStepCheckVo stepVo = new PackageDeviceCheckDetailStepCheckVo();
                stepVo.setStep(packageDeviceCheck.getStep());
                if (!typeStepsMap.isEmpty() && typeStepsMap.stream().filter(tsm -> tsm.get("step").equals(packageDeviceCheck.getStep())).findAny().isPresent()){
                    stepVo.setQty((Integer) typeStepsMap.stream().filter(tsm -> tsm.get("step").equals(packageDeviceCheck.getStep())).findAny().get().get("amount"));
                }else{
                    stepVo.setQty(packageConfigDetail.getAmount());
                }
                stepCheckVos.add(stepVo);
            }
            deviceTypeResponse.setStepChecks(stepCheckVos);
            deviceTypesForResponse.add(deviceTypeResponse);
        }
        response.setDeviceTypes(deviceTypesForResponse);
        return response;
    }

    private List<DeviceTypeImagesVo> doMainImage(Long typeId){
        List<DeviceTypeImagesVo> images = new ArrayList<DeviceTypeImagesVo>();
        List<Map<String, Object>> deviceImageMaps = deviceImageRepository.queryForImagesByTypeId(typeId);
        if (deviceImageMaps.isEmpty()){
            throw new AppException("此器械圖片設定有誤");
        }
        for(Map<String, Object> deviceImageMap : deviceImageMaps){
            if (Boolean.parseBoolean(deviceImageMap.get("isMain").toString()) == false){
                continue;
            }
            DeviceTypeImagesVo imageVo = new DeviceTypeImagesVo();
            imageVo.setId(Long.valueOf(deviceImageMap.get("id").toString()));
            imageVo.setPath(deviceImageMap.get("path").toString());
            imageVo.setIsMain(Boolean.parseBoolean(deviceImageMap.get("isMain").toString()));
            images.add(imageVo);
        }
        return images;
    }
    
    @Transactional(rollbackOn = Exception.class)
    public void doProcess(Long accountId, Long trackingId, PackageDeviceCheckDto packageDeviceCheckDto) throws Exception{
        Optional<Package> packOpt = packageRepository.findByTrackingId(trackingId);
        Package pack = packOpt.orElseThrow(() -> new AppException("查無包盤紀錄"));
        if (packageDeviceCheckDto.getStep() == 1 && packageDeviceCheckDto.getSterilizedSuccess() == null) {
            throw new Exception("sterilizedSuccess must not be null while step 1");
        }
        if (!checkPackageStatus(pack, packageDeviceCheckDto)){
            throw new AppException("包盤狀態異常");
        }
        updateCheckAndDiff(accountId, pack, packageDeviceCheckDto);
        changeStatus(pack, accountId, packageDeviceCheckDto.getStep(), packageDeviceCheckDto.getSterilizedSuccess());
    }
    
    private boolean checkPackageStatus(Package pack, PackageDeviceCheckDto packageDeviceCheckDto){
        switch(packageDeviceCheckDto.getStep()){
            case 1:
                if (!pack.getStatus().equals(PackageStatus.DISTRIBUTE.getValue())){
                    log.error("包盤ID: " + pack.getId() + ", 狀態錯誤");
                    return false;
                }
                break;
            case 2:
                if (!pack.getStatus().equals(PackageStatus.IN_USE.getValue()) && !pack.getStatus().equals(PackageStatus.PACK_INDICATOR_FAIL.getValue())){
                    log.error("包盤ID: " + pack.getId() + ", 狀態錯誤");
                    return false;
                }
                break;
            default:
                log.error("無效的step");
                return false;
        }
        return true;
    }
    
    private void updateCheckAndDiff(Long accountId, Package pack, PackageDeviceCheckDto dto) {
        List<PackageConfigDetail> detail = pack.getPackageConfig().getPackageConfigDetails();
        if(detail.size()!=dto.getDeviceTypes().size()) {
            log.error("器械類型的類型數量不正確，package_config_detail 的需求數量:"+detail.size()+", 傳入的數量: "+dto.getDeviceTypes().size());
            throw new AppException("器械類型的類型數量不正確");
        }
        Map<Long, PackageConfigDetail> packageConfigDetailMap = detail.stream().collect(Collectors.toMap(PackageConfigDetail::getDeviceTypeId, packageConfigDetail -> packageConfigDetail));
        //INSERT PACKAGE_DEVICE_CHECK
        PackageDeviceCheck packageDeviceCheck = insertPackageDeviceCheck(accountId, pack, dto.getStep());
        for(DeviceTypeVo deviceType : dto.getDeviceTypes()) {
            PackageConfigDetail configDetail = packageConfigDetailMap.get(deviceType.getId());
            if(configDetail==null) {
                log.error("deviceTypeId:"+deviceType.getId()+"，與config所需之deviceType不符");
                throw new AppException("輸入之器械類型與所需之器械類型不相符"); 
            }
            if(!configDetail.getAmount().equals(deviceType.getQty())) {
                Integer diff = deviceType.getQty()-configDetail.getAmount();
                //INSERT DEVICE_CHECK_DIFF
                insertDeviceCheckDiff(packageDeviceCheck.getId(), deviceType.getId(), deviceType.getQty(), diff);
            }
        }
    }
    
    private PackageDeviceCheck insertPackageDeviceCheck(Long accountId, Package pack, Integer step) {
        PackageDeviceCheck packageDeviceCheck = new PackageDeviceCheck();
        packageDeviceCheck.setStep(step);
        packageDeviceCheck.setTrackingId(pack.getTrackingId());
        packageDeviceCheck.setCreator(accountId.intValue());
        packageDeviceCheck.setCreateTime(new Date());
        packageDeviceCheck.setUpdator(accountId.intValue());
        packageDeviceCheck.setUpdateTime(new Date());
        return packageDeviceCheckRepository.saveAndFlush(packageDeviceCheck);
    }
    
    private void insertDeviceCheckDiff(Long packageDeviceCheckId, Long deviceTypeId, Integer amount, Integer diff) {
        DeviceCheckDiff deviceCheckDiff = new DeviceCheckDiff();
        deviceCheckDiff.setPackageDeviceCheckId(packageDeviceCheckId);
        deviceCheckDiff.setDeviceTypeId(deviceTypeId);
        deviceCheckDiff.setAmount(amount);
        deviceCheckDiff.setDifference(diff);
        deviceCheckDiffRepository.save(deviceCheckDiff);
    }
    
    private void changeStatus(Package pack, Long accountId, Integer step, Boolean sterilizedSuccess){
        switch(step){
            case 1:
                SterilizedBatchDetail sterilizedBatchDetail = sterilizedBatchDetailRepository.findFirstByTrackingIdOrderByCreateTimeDesc(pack.getTrackingId());
                if (sterilizedSuccess){
                    pack.setStatus(PackageStatus.IN_USE.getValue());
                    insertTrackingRecord(pack.getTrackingId(), PackageStatus.IN_USE.getValue(), accountId);
                }else{
                    pack.setStatus(PackageStatus.PACK_INDICATOR_FAIL.getValue());
                    insertTrackingRecord(pack.getTrackingId(), PackageStatus.PACK_INDICATOR_FAIL.getValue(), accountId);
                }
                sterilizedBatchDetail.setStatus(( sterilizedSuccess ) ? 1 : 0);
                sterilizedBatchDetailRepository.save(sterilizedBatchDetail);
                updatePackageOrderMappingStatus(pack.getId(), pack.getTrackingId(), PackageOrderMappingStatus.IN_USE.getValue());
                checkPackageOrderAndSurgicalApply(pack, PackageOrderMappingStatus.IN_USE.getValue(), PackageOrderStatus.IN_USE.getValue(), SurgicalApplyStatus.USING.getValue());
                break;
            case 2:
                pack.setStatus(PackageStatus.USE_DONE.getValue());
                insertTrackingRecord(pack.getTrackingId(), PackageStatus.USE_DONE.getValue(), accountId);
                updatePackageOrderMappingStatus(pack.getId(), pack.getTrackingId(), PackageOrderMappingStatus.FINISHED.getValue());
                checkPackageOrderAndSurgicalApply(pack, PackageOrderMappingStatus.FINISHED.getValue(), PackageOrderStatus.FINISH.getValue(), SurgicalApplyStatus.DONE.getValue());
                break;
            default:
                break;
        }
    }
    
    private void updatePackageOrderMappingStatus(Long packageId, Long trackingId, Integer status) {
        PackageOrderMapping pom = packageOrderMappingRepository.findByPackageIdAndTrackingId(packageId, trackingId);
        pom.setStatus(status);
    }
    
    private void insertTrackingRecord(Long trackingId, Integer status, Long accountId) {
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.setTrackingId(trackingId);
        trackingRecord.setStatus(status);
        trackingRecord.setCreator(accountId);
        trackingRecord.setCreateTime(new Date());
        trackingRecordRepository.save(trackingRecord);
    }
    
    private void checkPackageOrderAndSurgicalApply(Package pack, int packageOrderMappingStatus, int packageOrderStatus, int surgicalApplyStatus){
        PackageOrder packageOrder = pack.getPackageOrderMapping().getPackageOrder();
        SurgicalApply surgicalApply = packageOrder.getSurgicalApply();
        if (packageOrderMappingStatus == PackageOrderMappingStatus.IN_USE.getValue()){
            if(packageOrder.getStatus() != packageOrderStatus) {
                packageOrder.setStatus(packageOrderStatus);
            }
            List<Long> orderIds = surgicalApply.getOrders().stream().map(o -> o.getId()).collect(Collectors.toList());
            List<PackageOrderMapping> packageOrderMappings = packageOrderMappingRepository.findByPackageOrderIdInAndStatus(orderIds, PackageOrderMappingStatus.NOT_CHECK.getValue());
            if (packageOrderMappings.isEmpty()){
                surgicalApply.setStatus(surgicalApplyStatus);
            }
        }else{
            if (!packageOrder.getPackageOrderMappings().stream().filter(x -> x.getStatus() != packageOrderMappingStatus).findAny().isPresent()){
                packageOrder.setStatus(packageOrderStatus);
                if (!surgicalApply.getOrders().stream().filter(x -> x.getStatus() != packageOrderStatus).findAny().isPresent()){
                    surgicalApply.setStatus(surgicalApplyStatus);
                }
            }
        }
    }
    
    @Transactional(rollbackOn = Exception.class)
    public void scanProcess(Long accountId, Long trackingId, List<DeviceCheckGetInfoVo> devices) {
        Optional<Package> packageOpt = packageRepository.findByTrackingId(trackingId);
        Package pack = packageOpt.orElseThrow(()->new AppException("查無包盤資料"));
        List<Device> packageDeviceList = new ArrayList<Device>();
        for(Device device : pack.getDevice()){
            if(ComponentStatusFlag.IN_PACKAGE.getValue().equals(device.getStatus())) {
                packageDeviceList.add(device);
            }
        }
        if(CollectionUtils.isEmpty(packageDeviceList)) {
            doUpdateProcess(accountId, trackingId, devices, pack, true);
            return;
        }
        if(checkCorrespondingDevcieId(packageDeviceList, devices)) {
            doUpdateProcess(accountId, trackingId, devices, pack, false);
        }else {
            throw new AppException("ID及數量檢查失敗");
        }
    }
    
    private boolean checkCorrespondingDevcieId(List<Device> originDevices, List<DeviceCheckGetInfoVo> requestDevices) {
        Map<Long, Device> originDeviceMap = originDevices.stream().collect(Collectors.toMap(Device::getId, device->device));
        for(DeviceCheckGetInfoVo device : requestDevices){
            Device temp = originDeviceMap.get(device.getId());
            if(temp!=null) {
                originDeviceMap.remove(device.getId());
            }
        }
        return CollectionUtils.isEmpty(originDeviceMap);
    }
    
    private void doUpdateProcess(Long accountId, Long trackingId, List<DeviceCheckGetInfoVo> requestDevices, Package pack, boolean isAllDeviceScanned) {
        if(isAllDeviceScanned) {
            updatePackage(pack, accountId, trackingId);
        }else {
            updateDevices(requestDevices, pack, accountId, trackingId);
            updatePackage(pack, accountId, trackingId);
        }
    }
    
    private void updateDevices(List<DeviceCheckGetInfoVo> requestDevices, Package pack, Long accountId, Long trackingId) {
        Map<Long, Integer> requestDevicesMap = requestDevices.stream().collect(Collectors.toMap(DeviceCheckGetInfoVo::getId, devcie->devcie.getStatus()));
        List<Long> deviceIds = requestDevicesMap.keySet().stream().collect(Collectors.toList());
        List<Device> devices = deviceRepository.findAllById(deviceIds);
        if(CollectionUtils.isEmpty(devices)){
            throw new AppException("器械資料錯誤，查無器械紀錄");
        }
        for(Device device : devices){
            Integer status = requestDevicesMap.get(device.getId());
            device.setStatus(status);
            device.setUsedCount(device.getUsedCount()+1);
            device.setUpdateTime(new Date());
            if(ComponentStatusFlag.MISSING.getValue().equals(status)) {
                updatePakcageDevcieRecord(device.getId(), pack.getId());
            }
            if(ComponentStatusFlag.MISSING.getValue().equals(status)||
                ComponentStatusFlag.UNSCANABLE.getValue().equals(status)) {
                deviceCheckLogRepository.save(genDeviceCheckLog(trackingId, device.getId(), status, accountId, 0));
            }
        }
    }
    
    private void updatePakcageDevcieRecord(Long deviceId, Long packageId) {
        Optional<PackageDeviceRecord> pdrecordOpt = packageDeviceRecordRepository.findByPackageIdAndDeviceId(packageId, deviceId);
        PackageDeviceRecord pdrecord = pdrecordOpt.orElseThrow(()->{ 
            log.error("更新PACKAGE_DEVCIE_RECORD失敗, packageID: "+packageId+", deviceId: "+deviceId+" 查無資料");
            return new AppException("查無包盤器械資訊");
        });
        if(null==pdrecord.getDeviceOutTime()) {
            pdrecord.setDeviceOutTime(new Date());
        }
    }
    
    private DeviceCheckLog genDeviceCheckLog(Long trackingId, Long deviceId, Integer status, Long accountId, Integer type) {
        DeviceCheckLog deviceCheckLog = new DeviceCheckLog();
        deviceCheckLog.setTrackingId(trackingId);
        deviceCheckLog.setDeviceId(deviceId);
        deviceCheckLog.setStatus(status);
        deviceCheckLog.setType(type);
        deviceCheckLog.setCreateTime(new Date());
        deviceCheckLog.setCreator(accountId);
        return deviceCheckLog;
    }
    
    private void updatePackage(Package pack, Long accountId, Long trackingId) {
        if(!PackageStatus.USE_DONE.getValue().equals(pack.getStatus())) {
            log.error("包盤狀態更新失敗，Package ID: "+pack.getId()+", 目前狀態: "+pack.getStatus());
            throw new AppException("包盤狀態錯誤");
        }
        pack.setStatus(PackageStatus.CIRCULATION.getValue());
        pack.setDeviceBoxQrcode(null);
        pack.getDeviceBox().setStatus(ComponentStatusFlag.REPLACED.getValue());
        pack.getDeviceBox().setUpdateTime(new Date());
        pack.getPackageOrderMapping().setStatus(PackageOrderMappingStatus.CIRCULATE_CHECK.getValue());
        packageRepository.saveAndFlush(pack);
        insertTrackingRecord(trackingId, PackageStatus.CIRCULATION.getValue(), accountId);
        SurgicalApply surgicalApply=doPackageOrderCheck(pack.getPackageOrderMapping().getPackageOrder());
        checkAllOrderStatusOfSugicalApply(surgicalApply);
    } 
    
    private SurgicalApply doPackageOrderCheck(PackageOrder packageOrder) {
        List<PackageOrderMapping> packageOrderMappings = packageOrderMappingRepository.findByPackageOrderId(packageOrder.getId());
        if(CollectionUtils.isEmpty(packageOrderMappings)) {
            log.error("查無PackageOrderMapping, package order Id: "+packageOrder.getId());
            throw new AppException("清點失敗，查無PackageOrderMapping資料");
        }
        int count=0;
        for(PackageOrderMapping packageOrderMapping : packageOrderMappings){
            if(!PackageOrderMappingStatus.CIRCULATE_CHECK.getValue().equals(packageOrderMapping.getStatus())) {
                count++;
                break;
            }
        }
        if(count==0) {
            packageOrder.setStatus(PackageOrderStatus.CIRCULATE_CHECK_DONE.getValue());
            packageOrderRepository.saveAndFlush(packageOrder);
        }
        return packageOrder.getSurgicalApply();
    }
    
    private void checkAllOrderStatusOfSugicalApply(SurgicalApply surgicalApply) {
        Optional<List<PackageOrder>> packageOrdersOpt = packageOrderRepository.findBySurgicalApplyId(surgicalApply.getId());
        List<PackageOrder> packageOrders = packageOrdersOpt.orElseThrow(()->{
            log.error("查無對應orders, surgicalApplyId: "+surgicalApply.getId());
            return new AppException("手術申請資料錯誤");
        });
        int count=0;
        for(PackageOrder packageOrder : packageOrders){
            if(!PackageOrderStatus.CIRCULATE_CHECK_DONE.getValue().equals(packageOrder.getStatus())) {
                count++;
                break;
            }
        }
        if(count==0) {
            surgicalApply.setStatus(SurgicalApplyStatus.CIRCULATE_DONE.getValue());
            surgicalApplyRepository.saveAndFlush(surgicalApply);
        }
    }
}
