package com.surgical.services;

import com.surgical.entities.Device;
import com.surgical.entities.DeviceCheckLog;
import com.surgical.entities.DeviceImage;
import com.surgical.entities.DeviceModel;
import com.surgical.entities.DeviceRepairRecord;
import com.surgical.entities.DeviceType;
import com.surgical.entities.FileResource;
import com.surgical.entities.Package;
import com.surgical.enums.ComponentStatusFlag;
import com.surgical.exception.AppException;
import com.surgical.repositories.DeviceCheckLogRepository;
import com.surgical.repositories.DeviceImageRepository;
import com.surgical.repositories.DeviceModelRepository;
import com.surgical.repositories.DeviceRepairRecordRepository;
import com.surgical.repositories.DeviceRepository;
import com.surgical.repositories.DeviceTypeRepository;
import com.surgical.repositories.FileResourceRepository;
import com.surgical.repositories.PackageDeviceRecordRepository;
import com.surgical.repositories.TrackingRepository;
import com.surgical.vo.CreateDevice;
import com.surgical.vo.DeviceCheckGetInfoVo;
import com.surgical.vo.DeviceDetailRequest;
import com.surgical.vo.DeviceDetailUseRecord;
import com.surgical.vo.DeviceDetailVo;
import com.surgical.vo.DeviceImageVo;
import com.surgical.vo.DeviceListVo;
import com.surgical.vo.DeviceRepairRecordVo;
import com.surgical.vo.DeviceStatusOperation;
import com.surgical.vo.DeviceTypeImagesVo;
import com.surgical.vo.DevicesRequest;

import ch.qos.logback.core.pattern.Converter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Service
@Slf4j
public class DeviceService{

    private final static String uploadPath = "/images/";
    
    @Autowired
    private DeviceTypeRepository deviceTypeRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private FileResourceRepository fileResourceRepository;
    
    @Autowired
    private DeviceRepairRecordRepository deviceRepairRecordRepository;
    
    @Autowired 
    private DeviceImageRepository deviceImageRepository;
    
    @Autowired 
    private DeviceModelRepository deviceModelRepository;
    
    @Autowired
    private PackageDeviceRecordRepository packageDeviceRecordRepository;
    
    @Autowired
    private TrackingRepository trackingRepository;
    
    @Autowired
    private DeviceCheckLogRepository deviceCheckLogRepository;
    
    @PersistenceContext
    private EntityManager em;
    
    public Long saveDeviceImageFile(String ip, String fullFileName, Long fileSize, Long fileId){
        FileResource fileResource = new FileResource();
        Date now = new Date();
        if (fileId > 0){
            Optional<FileResource> fr = fileResourceRepository.findById(fileId);
            if (fr.isPresent()){
                fileResource = fr.get();
            }else{
                throw new AppException("invalid fileId");
            }
        }else {
            fileResource.setCreateTime(now);
        }
        String[] spilt = fullFileName.split("\\.");
        fileResource.setUpdateTime(now);
        fileResource.setStatus(0);
        fileResource.setResourceName(spilt[0]);
        fileResource.setContentType(spilt[1]);
        fileResource.setLocalPath(uploadPath + fullFileName);
        fileResource.setFileSize(fileSize);
        fileResource.setUploadIp(ip);
        return fileResourceRepository.save(fileResource).getId();
    }
    
    @Transactional
    public void updateStatus(DeviceStatusOperation deviceInfo, ComponentStatusFlag status){
        Long deviceId = deviceInfo.getDeviceId();
        Integer updater = deviceInfo.getUpdater();
        Integer statusCode = status.getValue();
        Optional<Device> deviceOption = deviceRepository.findById(deviceId);
        if (deviceOption.isPresent()){
            Integer currStatus = deviceOption.get().getStatus();
            switch(status){
                case REPAIR:
                    //?????????????????????0???2????????????
                    if(currStatus==ComponentStatusFlag.NORMAL.getValue()||currStatus==ComponentStatusFlag.REPLACED.getValue()) {
                        updateDeviceOBJ(deviceId, statusCode, Long.valueOf(updater), deviceOption);
                        deviceRecordProccessing(statusCode, deviceInfo, deviceOption.get().getUsedCount());
                    }else {
                        throw new AppException("Invalid status: "+currStatus+", only status NORMAL & REPLACED can be repaired!");
                    }
                    break;
                case REPAIR_DONE:
                    //?????????????????????4??????????????????
                    if(currStatus==ComponentStatusFlag.REPAIR.getValue()) {
                        updateDeviceOBJ(deviceId, ComponentStatusFlag.REPLACED.getValue(), Long.valueOf(updater), deviceOption);
                        updateRepairRecord(deviceId, statusCode);
                    }else {
                        throw new AppException("Invalid status: "+currStatus+", only status REPAIR can be set to REPAIR_DONE!");
                    }
                    break;
                case SCRAPPED:
                    if(currStatus==ComponentStatusFlag.NORMAL.getValue()
                        ||currStatus==ComponentStatusFlag.REPLACED.getValue()
                        ||currStatus==ComponentStatusFlag.REPAIR.getValue()) {
                        updateDeviceOBJ(deviceId, statusCode, Long.valueOf(updater), deviceOption);
                        deviceRecordProccessing(statusCode, deviceInfo, deviceOption.get().getUsedCount());
                    }else {
                        throw new AppException("Invalid status: "+currStatus+", only status NORMAL & REPLACED & REPAIR can be set to SCRAPPED!");
                    }
                    break;
                case DELETE:
                    if(currStatus==ComponentStatusFlag.NORMAL.getValue()) {
                        deviceRepository.delete(deviceOption.get());
                        log.info("Delete device object, device ID: "+deviceId+", updater: "+updater);
                    }else {
                        throw new AppException("Invalid status: "+currStatus+", only status NORMAL can be set to DELETE!");
                    }
                    break;
                default:
                    throw new AppException("Invalid device status!");
            }
        }else{
            throw new AppException("Invalid device id!");
        }
    }
    
    private void updateDeviceOBJ(Long id, Integer status, Long updater, Optional<Device> deviceOption) {
        Device device = deviceOption.get();
        device.setStatus(status);
        device.setUpdater(updater);
        device.setUpdateTime(new Date());
        if(status == ComponentStatusFlag.SCRAPPED.getValue()) {
            device.setScrapTime(new Date());
        }
        deviceRepository.save(device);
    }
    
    private void deviceRecordProccessing(Integer status, DeviceStatusOperation deviceInfo, Long totalUsedCount) {
        DeviceRepairRecord record = new DeviceRepairRecord();
        record.setDeviceId(deviceInfo.getDeviceId());
        record.setStatus(status);
        record.setUpdater(deviceInfo.getUpdater());
        record.setPreviousUsedCount(getPreviousUsedCount(deviceInfo.getDeviceId(),totalUsedCount));
        record.setDescription(deviceInfo.getDescription());
        record.setCreateTime(new Date());
        deviceRepairRecordRepository.save(record);
    }
    
    private Long getPreviousUsedCount(Long deviceId, Long totalUsedCount) {
        Optional<Long> previousUsedCount = deviceRepairRecordRepository.findUsedCountBeforeRepair(deviceId);
        if(previousUsedCount.isPresent()) {
            return totalUsedCount-previousUsedCount.get();
        }else {
            return totalUsedCount;
        }
        
    }
    
    private void updateRepairRecord(Long deviceId, Integer status) {
        Optional<DeviceRepairRecord> record = deviceRepairRecordRepository.findByDeviceIdAndStatusOrderByCreateTimeDesc(deviceId, ComponentStatusFlag.REPAIR.getValue());
        if(!record.isPresent()) {
            throw new AppException("Update DEVICE_REPAIR_RECORD failed, device record does not exist!");
        }else {
            DeviceRepairRecord newRecord = record.get();
            newRecord.setStatus(ComponentStatusFlag.REPAIR_DONE.getValue());
            newRecord.setFinishTime(new Date());                    
            deviceRepairRecordRepository.save(newRecord);
        }
    }
    
    public DeviceDetailVo deviceDetail(DeviceDetailRequest request) throws ParseException{
        //restructure
        DeviceDetailVo result = new DeviceDetailVo();
        Optional<Device> deviceOpt = deviceRepository.findByQrcode(request.getQrcode());
        Device device = deviceOpt.orElseThrow(() -> new AppException("QRCode??????"));
        DeviceType deviceType = device.getDeviceType();
        DeviceModel deviceModel = device.getDeviceModel();
        List<DeviceImage> deviceImages = device.getDeviceType().getDeviceImages();
        Package pack = device.getPack();
        List<DeviceRepairRecord> deviceRepairRecords = device.getDeviceRepairRecords();
        result.setName(deviceType.getName());
        result.setManufactureId(deviceModel.getManufactureId());
        result.setBrand(deviceModel.getBrand());
        result.setNameScientific(deviceType.getNameScientific());
        result.setSpec(deviceType.getSpec());
        result.setDesc(deviceType.getDescription());
        result.setImages(genImageVos(deviceImages));
        result.setStatus(device.getStatus());
        if (ComponentStatusFlag.IN_PACKAGE.getValue().equals(device.getStatus())){
            result.setPackageSerialNo(pack.getSerialNo());
        }
        result.setCreateTime(device.getCreateTime());
        result.setUsedTime(device.getUsedTime());
        result.setUdi(device.getUdi());
        result.setQrcode(device.getQrcode());
        result.setCost(device.getCost());
        result.setDivision(device.getDivision());
        result.setUsedCount(device.getUsedCount());
        result.setScrapTime(device.getScrapTime());
        result.setTypeId(deviceType.getId());
        result.setDeviceId(device.getId());
        result.setRepairRecords(genRepairRecordVos(device.getScrapTime(), deviceRepairRecords));
        result.setRepairCount(deviceRepairRecords.stream().filter(dr -> dr.getStatus() != (ComponentStatusFlag.SCRAPPED.getValue())).count());
        if (request.getHistory()){
            if (null == request.getStart() || null == request.getEnd()){
                throw new AppException("???????????????????????????????????????");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = sdf.parse(request.getStart() + " 00:00:00");
            Date end = sdf.parse(request.getEnd() + " 23:59:59");
            List<Map<String,Object>> trackings = packageDeviceRecordRepository.queryTrackingInfos(device.getId(), start, end);
            List<Long> trackingIds = trackings.stream().filter(m -> !m.get("trackingCreateTime").equals(m.get("out"))).map(m -> Long.parseLong(m.get("trackingId").toString())).collect(Collectors.toList());
            List<DeviceDetailUseRecord> records = new ArrayList<>();
            if(!trackingIds.isEmpty()) {
                records = trackingRepository.queryFromTrackingIdsForSurgeryInfo(trackingIds, start, end);
            }
            result.setUseRecords(records);
        }
        return result;
    }
    
    private List<DeviceImageVo> genImageVos(List<DeviceImage> deviceImages){
        List<DeviceImageVo> result = new ArrayList<DeviceImageVo>();
        for(DeviceImage deviceImage:deviceImages) {
            DeviceImageVo vo = new DeviceImageVo();
            vo.setId(deviceImage.getFileResource().getId());
            vo.setIsMain(deviceImage.getIsMain());
            vo.setPath(deviceImage.getFileResource().getLocalPath());
            result.add(vo);
        }
        return result;
    }
    
    private List<DeviceRepairRecordVo> genRepairRecordVos(Date scrapTime, List<DeviceRepairRecord> deviceRepairRecords){
        List<DeviceRepairRecordVo> result = new ArrayList<DeviceRepairRecordVo>();
        Collections.sort(deviceRepairRecords, new Comparator<DeviceRepairRecord>(){

            @Override
            public int compare(DeviceRepairRecord o1, DeviceRepairRecord o2){
                return o1.getCreateTime().compareTo(o2.getCreateTime());
            }
        });
        for(DeviceRepairRecord record : deviceRepairRecords) {
            DeviceRepairRecordVo vo = new DeviceRepairRecordVo();
            vo.setCreateTime(record.getCreateTime());
            vo.setDesc(record.getDescription());
            vo.setStatus(convertStatusString(record.getStatus()));
            vo.setUsedCount(record.getPreviousUsedCount());
            vo.setFinishTime((record.getStatus() == ComponentStatusFlag.SCRAPPED.getValue())? scrapTime : record.getFinishTime());
            result.add(vo);
        }
        return result;
    }
    
    private String convertStatusString(Integer status) {
        switch(status){
            case 4:
                return "?????????";
            case 5:
                return "????????????";
            case 9:
                return "??????";
            default:
                throw new AppException("Device status ??????");
        }
    }
    
    public Map<String, Object> deviceList(Long typeId, Integer division, Integer status, Pageable page){
        Map<String, Object> result = new HashMap<String, Object>();
        result.putAll(deviceRepository.countNumbers(typeId, division));
        Specification<Device> specification = new Specification<Device>(){
            @Override
            public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(criteriaBuilder.equal(root.get("typeId"), typeId));
                if (division != null){
                    predicates.add(criteriaBuilder.equal(root.get("division"), division));
                }
                if (status == null){
                    predicates.add(criteriaBuilder.notEqual(root.get("status"), 9));
                }else{
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        Page<Device> devicesPage = deviceRepository.findAll(specification, page);
        Page<DeviceListVo> deviceListPage = devicesPage.map(new Function<Device, DeviceListVo>(){
            @Override
            public DeviceListVo apply(Device device){
                DeviceListVo deviceListVo = new DeviceListVo();
                deviceListVo.setId(device.getId());
                deviceListVo.setStatus(device.getStatus());
                deviceListVo.setCreateTime(device.getCreateTime());
                deviceListVo.setUsedTime(device.getUsedTime());
                deviceListVo.setUdi(device.getUdi());
                deviceListVo.setQrcode(device.getQrcode());
                deviceListVo.setCost(device.getCost());
                deviceListVo.setDivision(device.getDivision());
                deviceListVo.setUsedCount(device.getUsedCount());
                deviceListVo.setScrapTime(device.getScrapTime());
                return deviceListVo;
            }
        });
        result.put("devices", deviceListPage);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Device> createDevices(DevicesRequest devicesRequest){
        List<CreateDevice> devicesInfoList = devicesRequest.getDevices();
        List<String> qrcodeQueryList = new ArrayList<String>();
        List<Device> deviceList = new ArrayList<Device>();
        Date now = new Date();
        Optional<DeviceType> deviceTypeOpt = deviceTypeRepository.findById(devicesRequest.getTypeId());
        if (!deviceTypeOpt.isPresent()){
            throw new AppException("DeviceType ?????????!");
        }
        for(CreateDevice createDevice : devicesInfoList){
            if (createDevice.getQrcode() == null || "".equals(createDevice.getQrcode())){
                throw new AppException("?????????Qrcode!");
            }
            qrcodeQueryList.add(createDevice.getQrcode());
            Device device = new Device();
            device.setCost(createDevice.getCost());
            device.setDivision(createDevice.getDivision());
            device.setQrcode(createDevice.getQrcode());
            device.setUpdater(Long.valueOf(devicesRequest.getUpdater()));
            device.setBranch(1);
            device.setCreateTime(now);
            device.setUpdateTime(now);
            device.setUsedCount(0L);
            device.setStatus(0);
            device.setTypeId(devicesRequest.getTypeId());
            device.setQrcode(createDevice.getQrcode());
            if (!StringUtils.isBlank(createDevice.getUdi())){
                device.setUdi(createDevice.getUdi());
            }
            if(createDevice.getDeviceModelId()!=null && createDevice.getDeviceModelId()!=0) {
                //Validating device model ID
                Optional<DeviceModel> deviceModelOpt =  deviceModelRepository.findById(createDevice.getDeviceModelId());
                deviceModelOpt.orElseThrow(()-> new AppException("??????Device Model"));
                device.setDeviceModelId(createDevice.getDeviceModelId());
            }else {
                if(createDevice.getDeviceModel()!=null) {
                    Long deviceModelId;
                    //Validating device model obj, create or query, depends on its existence
                    Optional<DeviceModel> deviceModelOpt = deviceModelRepository.findByBrandAndManufactureId(createDevice.getDeviceModel().getBrand(), createDevice.getDeviceModel().getManufactureId());
                    if(!deviceModelOpt.isPresent()) {
                        DeviceModel deviceModel = new DeviceModel(createDevice.getDeviceModel().getBrand(), createDevice.getDeviceModel().getManufactureId());
                        deviceModelId = deviceModelRepository.saveAndFlush(deviceModel).getId();
                    }else {
                        deviceModelId = deviceModelOpt.get().getId();
                    }
                    device.setDeviceModelId(deviceModelId);
                }else {
                    throw new AppException("Request????????????");
                }
            }
            deviceList.add(device);
        }
        List<String> qrcodes = deviceRepository.queryByQrcodeIn(qrcodeQueryList);
        if (!qrcodes.isEmpty()){
            throw new AppException("Qrcode ????????????!");
        }
        deviceRepository.saveAll(deviceList);
        return deviceList;
    }
    
    public DeviceCheckGetInfoVo checkDeviceGetInfo(String qrcode){
        Optional<Device> deviceOptional = deviceRepository.findByQrcode(qrcode);
        if (!deviceOptional.isPresent()){
            throw new AppException("??? qrcode ?????????????????????");
        }
        DeviceCheckGetInfoVo result = new DeviceCheckGetInfoVo();
        Device device = deviceOptional.get();
        result.setId(device.getId());
        result.setTypeId(device.getTypeId());
        result.setStatus(device.getStatus());
        result.setDivisionId(device.getDivision());
        result.setName(device.getDeviceType().getName());
//        result.setManufactureId(device.getDeviceType().getManufactureId());
//        result.setBrand(device.getDeviceType().getBrand());
        result.setNameScientific(device.getDeviceType().getNameScientific());
        result.setQrcode(device.getQrcode());
        result.setUdi(device.getUdi());
        List<DeviceTypeImagesVo> resultImages = new ArrayList<DeviceTypeImagesVo>();
        List<Map<String, Object>> deviceImageMaps = deviceImageRepository.queryForImagesByTypeId(device.getTypeId());
        if (deviceImageMaps.isEmpty()){
            throw new AppException("???????????????????????????");
        }
        for(Map<String, Object> deviceImageMap : deviceImageMaps){
            if(Boolean.parseBoolean(deviceImageMap.get("isMain").toString()) == false) {
                continue;
            }
            DeviceTypeImagesVo imageVo = new DeviceTypeImagesVo();
            imageVo.setId(Long.valueOf(deviceImageMap.get("id").toString()));
            imageVo.setPath(deviceImageMap.get("path").toString());
            imageVo.setIsMain(Boolean.parseBoolean(deviceImageMap.get("isMain").toString()));
            resultImages.add(imageVo);
        }
        result.setImages(resultImages);
        return result;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deviceReturn(String qrcode){
        Device device = deviceRepository.findByQrcode(qrcode).orElseThrow(() -> new AppException("????????? ??????id"));
        DeviceCheckLog log;
        if(device.getPackageId() != null) {
            throw new AppException("????????????????????????????????????????????????????????????????????????????????????????????????????????? " + device.getPack().getCode() );
        }
        try{
            log = deviceCheckLogRepository.findByDeviceIdAndReturnTimeIsNull(device.getId()).orElseThrow(() -> new AppException("???????????????????????????????????????"));
        }catch(IncorrectResultSizeDataAccessException e){
            throw new AppException("???????????????package_device_check ????????????????????????");
        }
        Date now = new Date();
        if (device.getStatus() == ComponentStatusFlag.UNSCANABLE.getValue() || device.getStatus() == ComponentStatusFlag.MISSING.getValue()){
            device.setStatus(ComponentStatusFlag.REPLACED.getValue());
            log.setReturnTime(now);
        }else{
            throw new AppException("??????????????????????????????????????????????????????????????????????????????");
        }
        deviceRepository.save(device);
        deviceCheckLogRepository.save(log);
    }
}
