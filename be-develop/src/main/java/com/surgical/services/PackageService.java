package com.surgical.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.surgical.entities.AddPackageAmountRecord;
import com.surgical.entities.Device;
import com.surgical.entities.DeviceBox;
import com.surgical.entities.DeviceCheckLog;
import com.surgical.entities.DeviceImage;
import com.surgical.entities.DeviceModel;
import com.surgical.entities.DeviceType;
import com.surgical.entities.Package;
import com.surgical.entities.PackageConfig;
import com.surgical.entities.PackageConfigDetail;
import com.surgical.entities.PackageDeviceRecord;
import com.surgical.entities.PackageGetInfoRequest;
import com.surgical.entities.Tracking;
import com.surgical.entities.TrackingRecord;
import com.surgical.enums.ActiveStatus;
import com.surgical.enums.ComponentStatusFlag;
import com.surgical.enums.PackageStatus;
import com.surgical.enums.StatusFlag;
import com.surgical.exception.AppException;
import com.surgical.repositories.AddPackageAmountRecordRepository;
import com.surgical.repositories.DeviceBoxRepository;
import com.surgical.repositories.DeviceCheckLogRepository;
import com.surgical.repositories.DeviceRepository;
import com.surgical.repositories.DeviceTypeRepository;
import com.surgical.repositories.DivisionRepository;
import com.surgical.repositories.PackageConfigDetailRepository;
import com.surgical.repositories.PackageConfigRepository;
import com.surgical.repositories.PackageDeviceRecordRepository;
import com.surgical.repositories.PackageRepository;
import com.surgical.repositories.TrackingRecordRepository;
import com.surgical.repositories.TrackingRepository;
import com.surgical.vo.AddAmountPackageRequest;
import com.surgical.vo.AddPackageAmountRecordRequest;
import com.surgical.vo.AddPackageAmountRecordResponse;
import com.surgical.vo.ApplyAddAmountRequest;
import com.surgical.vo.ApplyUpdateAmountRequest;
import com.surgical.vo.PackageDashboardResponseVo;
import com.surgical.vo.DeviceDetailVo;
import com.surgical.vo.DeviceImageVo;
import com.surgical.vo.DeviceTypeInfoVo;
import com.surgical.vo.DeviceTypeVo;
import com.surgical.vo.DeviceTypesQuantityInfo;
import com.surgical.vo.DivisionPackagesVo;
import com.surgical.vo.PackageApplyDetailResponseVo;
import com.surgical.vo.PackageConfigInfoResponseVo;
import com.surgical.vo.PackageConfigListVo;
import com.surgical.vo.PackageExpiredListRequest;
import com.surgical.vo.PackageExpiredListResponse;
import com.surgical.vo.PackageInfo;
import com.surgical.vo.PackageRequestVo;
import com.surgical.vo.PackageTrackingDeviceListVo;
import com.surgical.vo.PackageTrackingResponse;
import com.surgical.vo.ProcessRequest;
import com.surgical.vo.ProcessRequestDevicePart;
import com.surgical.vo.RepackReplacedDevicesRequest;
import com.surgical.vo.RepackRequest;
import com.surgical.vo.ReportPackageHistoryListRequest;
import com.surgical.vo.ReportPackageHistoryListResponse;
import com.surgical.vo.StationsVo;
import com.surgical.vo.StatusVo;


@Service
public class PackageService{
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private DeviceTypeRepository deviceTypeRepository;
    
    @Autowired
    private PackageRepository packageRepository;
    
    @Autowired
    private PackageConfigRepository packageConfigRepository;
    
    @Autowired
    private PackageConfigDetailRepository packageConfigDetailRepository;
    
    @Autowired
    private DivisionRepository divisionRepository;
    
    @Autowired
    private AddPackageAmountRecordRepository addPackageAmountRecordRepository;
    
    @Autowired
    private DeviceBoxRepository deviceBoxRepository;
    
    @Autowired
    private PackageDeviceRecordRepository packageDeviceRecordRepository;
    
    @Autowired
    private TrackingRepository trackingRepository;
    
    @Autowired
    private TrackingRecordRepository trackingRecordRepository;
    
    @Autowired
    private DeviceCheckLogRepository deviceCheckLogRepository;
    
    @Autowired
    CacheManager cacheManger;
    
    @PersistenceContext
    private EntityManager em;
    
    public void setExpiringDays(int expiringDays){
        Cache cache = cacheManger.getCache("packageExpiringDays");
        cache.put("packageExpiringDays", expiringDays);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void configCreate(Long accountId, PackageRequestVo packageRequest){
        if (!isDivisionExist(packageRequest.getDivisionId())){
            throw new AppException("科別不存在，Division ID : " + packageRequest.getDivisionId());
        }
        if (isPackageConfigExist(packageRequest.getConfigCode(), packageRequest.getConfigName(), packageRequest.getDivisionId())){
            throw new AppException("包盤類型已重複，無法新增！");
        }
        Date curDateTime = new Date();
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setCreator(accountId);
        packageConfig.setCreateTime(curDateTime);
        packageConfig.setUpdater(accountId);
        packageConfig.setUpdateTime(curDateTime);
        packageConfig.setPackageName(packageRequest.getConfigName());
        packageConfig.setConfigCode(packageRequest.getConfigCode());
        packageConfig.setDivisionId(packageRequest.getDivisionId());
        packageConfig.setIsActive(ActiveStatus.ENABLE.getValue());
        packageConfigRepository.save(packageConfig);
        List<PackageConfigDetail> packageConfigDetails = new ArrayList<PackageConfigDetail>();
        for(DeviceTypeVo deviceType : packageRequest.getDeviceTypes()){
            PackageConfigDetail packageConfigDetail = new PackageConfigDetail();
            packageConfigDetail.setDeviceTypeId(deviceType.getId());
            packageConfigDetail.setAmount(deviceType.getQty());
            packageConfigDetail.setPackageConfigId(packageConfig.getId());
            packageConfigDetails.add(packageConfigDetail);
        }
        packageConfig.setPackageConfigDetails(packageConfigDetails);
        packageConfigRepository.save(packageConfig);
    }

    private boolean isDivisionExist(Long divisionId){
        return divisionRepository.findByIdAndStatus(divisionId, StatusFlag.NORMAL.getValue()).isPresent();
    }

    private boolean isPackageConfigExist(String configCode, String configName, Long divisionId){
        return packageConfigRepository.findByConfigCodeAndPackageNameAndDivisionId(configCode, configName, divisionId).isPresent();
    }

    public Page<AddPackageAmountRecordResponse> applyList(AddPackageAmountRecordRequest request, Pageable page){
        Specification<AddPackageAmountRecord> specification = new Specification<AddPackageAmountRecord>(){
            @Override
            public Predicate toPredicate(Root<AddPackageAmountRecord> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder){
                List<Predicate> predicates = new ArrayList<Predicate>();
                Join<AddPackageAmountRecord, PackageConfig> packageConfigJoin = root.join("packageConfig", JoinType.LEFT);
                if (request.getStatus() == null){
                    predicates.add(root.get("status").in(ComponentStatusFlag.APAR_APPLY.getValue(), ComponentStatusFlag.APAR_RROCESSING.getValue()));
                }else{
                    predicates.add(builder.equal(root.get("status"), request.getStatus()));
                }
                
                if (request.getDivisionId() != null){
                    predicates.add(builder.equal(packageConfigJoin.get("divisionId"), request.getDivisionId()));
                }
                if (request.getConfigName() != null){
                    predicates.add(builder.like(packageConfigJoin.get("packageName"), "%" + request.getConfigName() + "%"));
                }
                if(request.getConfigCode() != null) {
                    predicates.add(builder.like(packageConfigJoin.get("configCode"), "%" + request.getConfigCode() + "%"));
                }
                if ("ASC".equalsIgnoreCase(request.getOrderBy())){
                    Order asc = builder.asc(root.get("createTime"));
                    criteriaQuery.orderBy(asc);
                }else{
                    Order desc = builder.desc(root.get("createTime"));
                    criteriaQuery.orderBy(desc);
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        Page<AddPackageAmountRecord> recordPage = addPackageAmountRecordRepository.findAll(specification, page);
        Page<AddPackageAmountRecordResponse> result = recordPage.map(new Function<AddPackageAmountRecord, AddPackageAmountRecordResponse>(){
            @Override
            public AddPackageAmountRecordResponse apply(AddPackageAmountRecord record){
                AddPackageAmountRecordResponse addPackageAmountRecordResponse = new AddPackageAmountRecordResponse();
                addPackageAmountRecordResponse.setId(record.getId());
                addPackageAmountRecordResponse.setStatus(record.getStatus());
                addPackageAmountRecordResponse.setConfigId(record.getPackageConfigId());
                Map<String, Integer> qtyMap = getQtyAndPackagedQty(record);
                addPackageAmountRecordResponse.setQty(qtyMap.get("qty"));
                addPackageAmountRecordResponse.setPackagedQty(qtyMap.get("packagedQty"));
                addPackageAmountRecordResponse.setConfigName(record.getPackageConfig().getPackageName());
                addPackageAmountRecordResponse.setDivisionId(record.getPackageConfig().getDivisionId());
                addPackageAmountRecordResponse.setConfigCode(record.getPackageConfig().getConfigCode());
                addPackageAmountRecordResponse.setPackages(genPackageInfoVoList(record.getPackages()));
                return addPackageAmountRecordResponse;
            }
        });
        return result;
    }
    
    private Map<String, Integer> getQtyAndPackagedQty(AddPackageAmountRecord apar) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        Integer qty=0;
        Integer packagedQty=0;
        if(!ComponentStatusFlag.APAR_ALLCANCEL.getValue().equals(apar.getStatus())){          
            List<Package> packages = packageRepository.findByAddId(apar.getId());
            if(packages.isEmpty()) {
                throw new AppException("資料異常");
            }
            qty = packages.size();
            packagedQty=0;
            for(Package pack: packages) {
                if(!PackageStatus.APPLY.getValue().equals(pack.getStatus())) {
                    packagedQty++;
                }
            }
        }
        result.put("qty", qty);
        result.put("packagedQty", packagedQty);
        return result;
    }
    
    public Page<PackageConfigListVo> getConfigList(Integer divisionId, Integer isActive, String configName, String configCode, Integer showCount, Pageable page){
        Specification<PackageConfig> specification = new Specification<PackageConfig>(){

            @Override
            public Predicate toPredicate(Root<PackageConfig> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (null != divisionId){
                    predicates.add(criteriaBuilder.equal(root.get("divisionId"), divisionId));
                }
                if (null != configName && !"".equals(configName)){
                    predicates.add(criteriaBuilder.like(root.get("packageName"), "%" + configName + "%"));
                }
                if (null != isActive){
                    predicates.add(criteriaBuilder.equal(root.get("isActive"), isActive));
                }
                if (null != configCode && !"".equals(configCode)){
                    predicates.add(criteriaBuilder.like(root.get("configCode"), "%" + configCode + "%"));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        Page<PackageConfig> packageconfigPage = packageConfigRepository.findAll(specification, page);
        Page<PackageConfigListVo> result = packageconfigPage.map(new Function<PackageConfig, PackageConfigListVo>(){

            @Override
            public PackageConfigListVo apply(PackageConfig packageconfigPage){
                PackageConfigListVo packageConfigListVo = new PackageConfigListVo();
                packageConfigListVo.setConfigId(packageconfigPage.getId());
                packageConfigListVo.setDivisionId(packageconfigPage.getDivisionId());
                packageConfigListVo.setConfigName(packageconfigPage.getPackageName());
                packageConfigListVo.setConfigCode(packageconfigPage.getConfigCode());
                packageConfigListVo.setIsActive(packageconfigPage.getIsActive());
                packageConfigListVo.setCreateTimeDate(packageconfigPage.getCreateTime());
                if(showCount!=null && showCount!=0) {
                    List<Integer> statuses = new ArrayList<Integer>();
                    statuses.add(PackageStatus.APPLY.getValue());
                    statuses.add(PackageStatus.UNPACK.getValue());
                    Map<String, Integer> map = getPackageQtyAndTotalUsedCount(packageconfigPage.getId(), statuses);
                    packageConfigListVo.setPackageQty(map.get("packageQty"));
                    packageConfigListVo.setTotalUsedCount(map.get("totalUsedCount"));
                    packageConfigListVo.setInStockQty(map.get("inStockQty"));
                }
                return packageConfigListVo;
            }
        });
        return result;
    }
    
    private Map<String, Integer> getPackageQtyAndTotalUsedCount(Long configId, List<Integer> statuses) {
        Map<String, Integer> resultMap = new HashMap<String, Integer>();
        List<Package> packages = packageRepository.findByPackageConfigIdAndStatusNotIn(configId, statuses);
        Integer totalUsedCount = 0;
        totalUsedCount = packages.stream().mapToInt(pack->pack.getUsedCount().intValue()).sum();
        Long inStockQty = packages.stream().filter(o -> o.getStatus() == PackageStatus.IN_STOCK.getValue()).count();
        resultMap.put("inStockQty", inStockQty.intValue());
        resultMap.put("packageQty", CollectionUtils.isEmpty(packages)?0:packages.size());
        resultMap.put("totalUsedCount", totalUsedCount);
        return resultMap;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void applyAddPackageAmount(ApplyAddAmountRequest request, Long accountId){
        Optional<PackageConfig> packageConfigOptional = packageConfigRepository.findById(request.getConfigId());
        if(!packageConfigOptional.isPresent()) {
            throw new AppException("此 包盤組合 不存在");
        }
        if(request.getPackages().isEmpty()) {
            throw new AppException("Request package list is empty.");
        }
        Date now = new Date();
        AddPackageAmountRecord addPackageAmountRecord = new AddPackageAmountRecord();
        addPackageAmountRecord.setPackageConfigId(request.getConfigId());
        addPackageAmountRecord.setCreateTime(now);
        addPackageAmountRecord.setCreator(accountId);
        addPackageAmountRecord.setUpdateTime(now);
        addPackageAmountRecord.setUpdater(accountId);
        addPackageAmountRecord.setStatus(ComponentStatusFlag.APAR_APPLY.getValue());
        Long addId = addPackageAmountRecordRepository.save(addPackageAmountRecord).getId();
        packageProcess(request, addId);
    }
    
    private void packageProcess(ApplyAddAmountRequest request, Long addId) {
        List<AddAmountPackageRequest> addAmountPackages = request.getPackages();
        List<String> serialNos = new ArrayList<String>();
        for(AddAmountPackageRequest addAmountPackageRequest : addAmountPackages){
            if(addAmountPackageRequest.getSerialNo().length()>20) {
                throw new AppException(addAmountPackageRequest.getSerialNo()+"長度超過20");
            }
            if (serialNos.contains(addAmountPackageRequest.getSerialNo())){
                throw new AppException("傳入的Serial number重複");
            }else {
                serialNos.add(addAmountPackageRequest.getSerialNo());
            }
        }
        for(AddAmountPackageRequest addAmountPackage: addAmountPackages) {
            if(checkDuplicatedSerialNo(null, addAmountPackage.getSerialNo(), 1)) {
                throw new AppException("包盤編號: "+addAmountPackage.getSerialNo()+" 已存在");
            }
            Optional<PackageConfig> configOpt = packageConfigRepository.findById(request.getConfigId());
            PackageConfig config = configOpt.orElseThrow(()-> new AppException("Invalid config ID"));
            String packageCode = genPackageCode(config);
            Package pack = new Package();
            pack.setAddId(addId);
            pack.setCode(packageCode);
            pack.setCreateTime(new Date());
            pack.setPackageConfigId(config.getId());
            pack.setSerialNo(addAmountPackage.getSerialNo());
            pack.setStatus(PackageStatus.APPLY.getValue());
            pack.setPosition(addAmountPackage.getPosition());
            packageRepository.saveAndFlush(pack);
        }
    }
    
    private String genPackageCode( PackageConfig config) {
        String result="";
        String prefix = String.format("%-15s", config.getConfigCode()).replace(' ', '0');
        TypedQuery<Package> query = em.createQuery("SELECT p FROM Package p WHERE p.code like '"+prefix+"%' ORDER BY p.code DESC", Package.class);    
        query.setMaxResults(1);
        List<Package> pack = query.getResultList();
        if(CollectionUtils.isEmpty(pack)) {
            result = prefix+"00001";
        }else {
            String numString = pack.get(0).getCode().substring(pack.get(0).getCode().length()-5, pack.get(0).getCode().length());
            Integer num = Integer.parseInt(numString)+1;
            String newNumString = String.format("%5s", num).replace(' ', '0');
            result = String.format("%-15s", config.getConfigCode()).replace(' ', '0') + newNumString;
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void applyUpdatePackageAmount(ApplyUpdateAmountRequest request, Long accountId){
        Validate.isTrue(ObjectUtils.allNotNull(request), "applyUpdateAmountRequest must not be null or empty");
        Optional<AddPackageAmountRecord> recordOptional = addPackageAmountRecordRepository.findById(request.getId());
        AddPackageAmountRecord record = recordOptional.orElseThrow(()->new AppException("此筆申請紀錄不存在"));
        if (record.getStatus() != 1 && record.getStatus() != 2){
            throw new AppException("此筆記錄狀態 非可編輯之狀態");
        }
        doComparisonAndDBOperation(request, record);
    }
    
    private void doComparisonAndDBOperation(ApplyUpdateAmountRequest request, AddPackageAmountRecord record) {
        List<AddAmountPackageRequest> requestList = request.getPackages();
        List<Package> insertList = new ArrayList<Package>();
        List<Package> updateList = new ArrayList<Package>();
        if(CollectionUtils.isEmpty(request.getRemovePackages())&&CollectionUtils.isEmpty(requestList)) {
            throw new AppException("沒有需變更的包盤");
        }
        if(!CollectionUtils.isEmpty(request.getRemovePackages())) {
            List<Package> removeList = packageRepository.findByAddIdAndStatusAndIdIn(record.getId() , PackageStatus.APPLY.getValue(), request.getRemovePackages());
            if(request.getRemovePackages().size()==removeList.size()) {
                packageRepository.deleteAll(removeList);
                packageRepository.flush();
            }else {
                throw new AppException("欲移除之包盤狀態不正確");
            }
        }
        if(CollectionUtils.isEmpty(requestList)) {
            return;
        }
        
        List<String> serialNos = new ArrayList<String>();
        List<Long> packageIds = new ArrayList<Long>();
        for(AddAmountPackageRequest addAmountPackageRequest : requestList){
            if(addAmountPackageRequest.getSerialNo().length()>20) {
                throw new AppException(addAmountPackageRequest.getSerialNo()+"長度超過20");
            }
            if (serialNos.contains(addAmountPackageRequest.getSerialNo())){
                throw new AppException("傳入的Serial number重複");
            }else {
                serialNos.add(addAmountPackageRequest.getSerialNo());
            }
            if (addAmountPackageRequest.getId() != null){
                packageIds.add(addAmountPackageRequest.getId());
            }
        }
        for(AddAmountPackageRequest aAPRequest : requestList) {
            if(null==aAPRequest.getId()) {
                if(checkDuplicatedSerialNo(null,aAPRequest.getSerialNo(),1)) {
                    throw new AppException("更新失敗，Serial number: "+aAPRequest.getSerialNo()+" 重複");
                }
                insertList.add(genPackageOBJ(aAPRequest, record));
            }else {
                Optional<Package> packageOpt = packageRepository.findById(aAPRequest.getId());
                Package pack = packageOpt.orElseThrow(()->new AppException("查無欲更新的紀錄"));
                if(!PackageStatus.APPLY.getValue().equals(pack.getStatus())) {
                    throw new AppException("更新失敗，狀態不正確"); 
                }
                if(checkDuplicatedSerialNo(packageIds,aAPRequest.getSerialNo(),0)) {
                    throw new AppException("更新失敗，Serial number: "+aAPRequest.getSerialNo()+" 重複");
                }
                pack.setSerialNo(aAPRequest.getSerialNo());
                pack.setPosition(aAPRequest.getPosition());
                updateList.add(pack);
            }
        }
        List<Package> finalInputList = Stream.of(insertList, updateList).flatMap(list->list.stream()).collect(Collectors.toList());       
        packageRepository.saveAll(finalInputList);
        packageRepository.flush();
        if(checkIfNoPackagesLeft(record.getId())) {
            AddPackageAmountRecord apar = addPackageAmountRecordRepository.getOne(record.getId());
            apar.setStatus(ComponentStatusFlag.APAR_ALLCANCEL.getValue());
        }
    }
    
    private boolean checkDuplicatedSerialNo(List<Long> requestIds, String requestSerialNo, Integer searchMode) {
        List<Package> packages=new ArrayList<Package>();
        switch(searchMode){
            case 0:  //not include self
                packages = packageRepository.findBySerialNoAndStatusNotAndIdNotIn(requestSerialNo, PackageStatus.UNPACK.getValue(), requestIds);                            
                break;
            case 1:  //through entire table
                packages = packageRepository.findBySerialNoAndStatusNot(requestSerialNo, PackageStatus.UNPACK.getValue());
                break;
            default:
                throw new AppException("Serial number 檢核失敗");
        }
        if(CollectionUtils.isEmpty(packages)) {
            return false;
        }
        return true;
    }
    
    private Package genPackageOBJ(AddAmountPackageRequest aAPRequest, AddPackageAmountRecord addPackageAmountRecord) {
        Package pack = new Package();
        pack.setAddId(addPackageAmountRecord.getId());
        pack.setCode(genPackageCode(addPackageAmountRecord.getPackageConfig()));
        pack.setCreateTime(new Date());
        pack.setPackageConfigId(addPackageAmountRecord.getPackageConfigId());
        pack.setSerialNo(aAPRequest.getSerialNo());
        pack.setPosition(aAPRequest.getPosition());
        pack.setStatus(PackageStatus.APPLY.getValue());
        return pack;
    }
    
    private boolean checkIfNoPackagesLeft(Long addId) {
        List<Package> packages = packageRepository.findByAddId(addId);
        if(packages.isEmpty()) {
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelAddPackageAmountRecord(ApplyUpdateAmountRequest cancelRequest, Long accountId){
        Validate.isTrue(ObjectUtils.allNotNull(cancelRequest), "CancelApplicationRequest must not be null or empty");
        Validate.isTrue(ObjectUtils.allNotNull(cancelRequest.getId()), "id cannot be null");
        Optional<AddPackageAmountRecord> addPackageAmountRecordOptional = addPackageAmountRecordRepository.findById(cancelRequest.getId());
        if (!addPackageAmountRecordOptional.isPresent()){
            throw new AppException("不存在此紀錄, id : " + cancelRequest.getId());
        }
        
        AddPackageAmountRecord addPackageAmountRecord = addPackageAmountRecordOptional.get();
        if (ComponentStatusFlag.APAR_ALLCANCEL.getValue() == addPackageAmountRecord.getStatus()){
            throw new AppException("此紀錄已經取消,id :" + cancelRequest.getId());
        }
        List<Package> packages = packageRepository.findByAddId(addPackageAmountRecord.getId());
        Boolean isCancelable = true;
        for(Package pack:packages) {
            if(!PackageStatus.APPLY.getValue().equals(pack.getStatus())) {
                isCancelable=false;
                break;
            }
        }
        if(!isCancelable) {
            throw new AppException("取消申請失敗，已有打包完成的包盤");
        }
        packageRepository.deleteAll(packages);
        cancelAddPackageAmountRecord(addPackageAmountRecord, accountId, new Date());
    }

    private AddPackageAmountRecord cancelAddPackageAmountRecord(AddPackageAmountRecord addPackageAmountRecord, Long accountId, Date now){
        addPackageAmountRecord.setUpdater(accountId);
        addPackageAmountRecord.setUpdateTime(now);
        addPackageAmountRecord.setStatus(ComponentStatusFlag.APAR_ALLCANCEL.getValue());
        addPackageAmountRecordRepository.save(addPackageAmountRecord);
        return addPackageAmountRecord;
    }
    
    public PackageConfigInfoResponseVo configDetail(Long configId){
        return getPackageConfigInfo(configId);
    }

    @Transactional(rollbackFor = Exception.class)
    public PackageApplyDetailResponseVo applyDetail(Long id){
        Optional<AddPackageAmountRecord> addPackageAmountRecord = addPackageAmountRecordRepository.findById(id);
        if (!addPackageAmountRecord.isPresent()){
            throw new AppException("此筆申請紀錄不存在，id : " + id);
        }
        PackageApplyDetailResponseVo packageApplyDetailResponse = new PackageApplyDetailResponseVo();
        packageApplyDetailResponse.setId(addPackageAmountRecord.get().getId());
        packageApplyDetailResponse.setConfigId(addPackageAmountRecord.get().getPackageConfigId());
        packageApplyDetailResponse.setStatus(addPackageAmountRecord.get().getStatus());
        Map<String, Integer> qtyMap = getQtyAndPackagedQty(addPackageAmountRecord.get());
        packageApplyDetailResponse.setApplyQty(qtyMap.get("qty"));
        packageApplyDetailResponse.setPackagedQty(qtyMap.get("packagedQty"));
        packageApplyDetailResponse.setPackages(genPackageInfoVoList(addPackageAmountRecord.get().getPackages()));
        return packageApplyDetailResponse;
    }
    
    private List<PackageInfo> genPackageInfoVoList(List<Package> packages){
        List<PackageInfo> result = new ArrayList<PackageInfo>();
        for(Package pack : packages){
            PackageInfo packageInfo = PackageInfo.builder()
                .id(pack.getId())
                .packageCode(pack.getCode())
                .serialNo(pack.getSerialNo())
                .position(pack.getPosition())
                .status(pack.getStatus()).build();
            result.add(packageInfo);
        }
        return result;
    }
    
    private PackageConfigInfoResponseVo getPackageConfigInfo(Long packageConfigId){
        Optional<PackageConfig> packageConfig = packageConfigRepository.findById(packageConfigId);
        if (!packageConfig.isPresent()){
            throw new AppException("此包盤組合不存在，id : " + packageConfigId);
        }
        Long totalQty = packageRepository.countByPackageConfigId(packageConfigId);
        List<PackageConfigDetail> packageConfigDetails = packageConfig.get().getPackageConfigDetails();
        DeviceTypesQuantityInfo deviceTypesQuantityInfo = getDeviceTypesQuantityInfo(packageConfigDetails);
        Map<Long, Integer> deviceTypeIdAndQuantity = deviceTypesQuantityInfo.getDeviceTypeIdAndQuantity();
        List<DeviceTypeInfoVo> deviceTypeInfos = setDeviceTypesInfo(deviceTypeIdAndQuantity);
        PackageConfigInfoResponseVo packageConfigInfoResponse = new PackageConfigInfoResponseVo();
        packageConfigInfoResponse.setConfigId(packageConfig.get().getId());
        packageConfigInfoResponse.setConfigCode(packageConfig.get().getConfigCode());
        packageConfigInfoResponse.setConfigName(packageConfig.get().getPackageName());
        packageConfigInfoResponse.setDivisionId(packageConfig.get().getDivision().getId());
        packageConfigInfoResponse.setTotalQty(totalQty);
        packageConfigInfoResponse.setDeviceTypes(deviceTypeInfos);
        packageConfigInfoResponse.setDeviceTypeQty(deviceTypeIdAndQuantity.size());
        packageConfigInfoResponse.setDeviceQty(deviceTypesQuantityInfo.getDeviceQty());
        return packageConfigInfoResponse;
    }

    private DeviceTypesQuantityInfo getDeviceTypesQuantityInfo(List<PackageConfigDetail> packageConfigDetails){
        Map<Long, Integer> deviceTypeIdAndQuantity = new HashMap<Long, Integer>();
        Integer totalDeviceQty = 0;
        for(PackageConfigDetail packageConfigDetail : packageConfigDetails){
            totalDeviceQty += packageConfigDetail.getAmount();
            if (deviceTypeIdAndQuantity.get(packageConfigDetail.getDeviceTypeId()) == null){
                deviceTypeIdAndQuantity.put(packageConfigDetail.getDeviceTypeId(), packageConfigDetail.getAmount());
                continue;
            }
            Integer deviceQty = deviceTypeIdAndQuantity.get(packageConfigDetail.getDeviceTypeId());
            deviceQty += packageConfigDetail.getAmount();
            deviceTypeIdAndQuantity.put(packageConfigDetail.getDeviceTypeId(), deviceQty);
        }
        DeviceTypesQuantityInfo deviceTypesQuantityInfo = new DeviceTypesQuantityInfo();
        deviceTypesQuantityInfo.setDeviceQty(totalDeviceQty);
        deviceTypesQuantityInfo.setDeviceTypeIdAndQuantity(deviceTypeIdAndQuantity);
        return deviceTypesQuantityInfo;
    }
    
    private List<DeviceTypeInfoVo> setDeviceTypesInfo(Map<Long, Integer> deviceTypeIdAndQuantity){
        List<DeviceTypeInfoVo> deviceTypeInfos = new ArrayList<DeviceTypeInfoVo>();
        if (deviceTypeIdAndQuantity != null && !deviceTypeIdAndQuantity.isEmpty()){
            for(Long typeId : deviceTypeIdAndQuantity.keySet()){
                Optional<DeviceType> fullDeviceTypeInfo = deviceTypeRepository.findById(typeId);
                if (fullDeviceTypeInfo.isPresent()){
                    DeviceTypeInfoVo deviceTypeInfo = new DeviceTypeInfoVo();
                    deviceTypeInfo.setTypeId(typeId);
                    deviceTypeInfo.setQty(deviceTypeIdAndQuantity.get(typeId));
                    deviceTypeInfo.setNameScientific(fullDeviceTypeInfo.get().getNameScientific());
                    deviceTypeInfo.setName(fullDeviceTypeInfo.get().getName());
                    deviceTypeInfo.setSpec(fullDeviceTypeInfo.get().getSpec());
                    deviceTypeInfo.setDesc(fullDeviceTypeInfo.get().getDescription());
                    List<DeviceImageVo> deviceImages = new ArrayList<DeviceImageVo>();
                    for(DeviceImage deviceImageInfo : fullDeviceTypeInfo.get().getDeviceImages()){
                        if (!deviceImageInfo.getIsMain()){
                            continue;
                        }
                        DeviceImageVo deviceImage = new DeviceImageVo();
                        deviceImage.setId(deviceImageInfo.getFileId());
                        deviceImage.setIsMain(deviceImageInfo.getIsMain());
                        deviceImage.setPath(deviceImageInfo.getFileResource().getLocalPath());
                        deviceImages.add(deviceImage);
                    }
                    deviceTypeInfo.setImages(deviceImages);
                    deviceTypeInfos.add(deviceTypeInfo);
                }
            }
        }
        return deviceTypeInfos;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void process(ProcessRequest request, Long accountId){
        Date now = new Date();
        Optional<Package> packageOpt = packageRepository.findById(request.getPackageId());
        Package pack = packageOpt.orElseThrow(()->new AppException("此筆Package紀錄不存在"));
        if(!pack.getStatus().equals(PackageStatus.APPLY.getValue())) {
            throw new AppException("Invalid package status.");
        }
        AddPackageAmountRecord record = pack.getAddPackageAmountRecord();
        List<Device> allDevices = DeviceAndDeviceBoxProcess(request, accountId, now, record.getPackageConfig());
        record.setUpdater(accountId);
        record.setUpdateTime(now);
        if (checkPackageIfLastOne(pack)){
            record.setStatus(ComponentStatusFlag.APAR_DONE.getValue());
        }else{
            record.setStatus(ComponentStatusFlag.APAR_RROCESSING.getValue());
        }
        addPackageAmountRecordRepository.save(record);
        List<PackageDeviceRecord> allPackageDeviceRecords = new ArrayList<PackageDeviceRecord>();
        for(Device device : allDevices){
            PackageDeviceRecord packageDeviceRecord = new PackageDeviceRecord();
            packageDeviceRecord.setCreator(accountId);
            packageDeviceRecord.setDeviceId(device.getId());
            packageDeviceRecord.setDeviceInTime(now);
            packageDeviceRecord.setPackageId(pack.getId());
            packageDeviceRecord.setUpdater(accountId);
            allPackageDeviceRecords.add(packageDeviceRecord);
        }
        deviceRepository.saveAll(allDevices);
        packageDeviceRecordRepository.saveAll(allPackageDeviceRecords);
        Tracking tracking = new Tracking();
        tracking.setCreateTime(now);
        tracking.setPackageId(pack.getId());
        trackingRepository.save(tracking);
        TrackingRecord trackingRecord = new TrackingRecord(tracking.getId(), 0, now, accountId);
        trackingRecordRepository.save(trackingRecord);
        pack.setTrackingId(tracking.getId());
        pack.setDeviceBoxQrcode(request.getDeviceBoxQrcode());
        pack.setStatus(PackageStatus.PACK_DONE.getValue());
        packageRepository.save(pack);
    }

    private List<Device> DeviceAndDeviceBoxProcess(ProcessRequest request, Long accountId, Date now, PackageConfig config){
        Integer[] statues = {
            ComponentStatusFlag.NORMAL.getValue(), ComponentStatusFlag.REPLACED.getValue()
        };
        //器械盒part
        Optional<DeviceBox> deviceBoxOptional = deviceBoxRepository.findByQrcode(request.getDeviceBoxQrcode());
        if (!deviceBoxOptional.isPresent()){
            throw new AppException("此 器械盒 qrcode 不存在");
        }
        DeviceBox deviceBox = deviceBoxOptional.get();
        if (deviceBox.getStatus() != ComponentStatusFlag.NORMAL.getValue() && deviceBox.getStatus() != ComponentStatusFlag.REPLACED.getValue()){
            throw new AppException("此 器械盒qrcode 狀態異常");
        }
        deviceBox.setStatus(ComponentStatusFlag.IN_PACKAGE.getValue());
        deviceBox.setUpdater(accountId);
        deviceBox.setUpdateTime(now);
        if (deviceBox.getUsedTime() == null){
            deviceBox.setUsedTime(now);
        }
        // 器械
        List<Device> allDevices = new ArrayList<Device>();
        List<ProcessRequestDevicePart> reqeustDeviceParts = request.getApplyDeviceList();
        List<PackageConfigDetail> packageConfigDetails = packageConfigDetailRepository.findByPackageConfigId(config.getId());
        if (reqeustDeviceParts.size() != packageConfigDetails.size()){
            throw new AppException("傳入的器械種類數量 和 config 設定的不同");
        }
        Map<Long, PackageConfigDetail> packageConfigMap = packageConfigDetails.stream().collect(Collectors.toMap(PackageConfigDetail::getDeviceTypeId, packageConfigDetail -> packageConfigDetail));
        for(ProcessRequestDevicePart requestDevicePart : reqeustDeviceParts){
            PackageConfigDetail packageConfigDetail = packageConfigMap.get(requestDevicePart.getDeviceTypeId());
            if (packageConfigDetail == null){
                throw new AppException("目前包盤的器械種類 與 config設定的不同");
            }
            List<Device> devices = deviceRepository.findAllByIdInAndTypeIdAndStatusInAndDivision(requestDevicePart.getDeviceIds(), requestDevicePart.getDeviceTypeId(), statues, config.getDivisionId().intValue());
            if (devices.size() != packageConfigDetail.getAmount()){
                throw new AppException("目前包盤的器械資格異常");
            }
            for(Device device : devices){
                device.setStatus(ComponentStatusFlag.IN_PACKAGE.getValue());
                device.setUpdater(accountId);
                device.setUpdateTime(now);
                if (device.getUsedTime() == null){
                    device.setUsedTime(now);
                }
                device.setPackageId(request.getPackageId());
                allDevices.add(device);
            }
            packageConfigMap.remove(requestDevicePart.getDeviceTypeId());
        }
        if (!packageConfigMap.isEmpty()){
            throw new AppException("目前包盤的器械種類 與 config 設定不同");
        }
        return allDevices;
    }
    
    private boolean checkPackageIfLastOne(Package pack) {
        int count = packageRepository.findByAddIdAndStatus(pack.getAddId(), PackageStatus.APPLY.getValue()).size();
        if (count>1) {
            return false;
        }else {
            return true;
        }
    }

    public PackageInfo getInfo(PackageGetInfoRequest packageGetInfoRequest){
        Optional<Package> packageOpt = packageRepository.findOne(new Specification<Package>(){

            @Override
            public Predicate toPredicate(Root<Package> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(null!=packageGetInfoRequest.getQrcode()) {
                    predicates.add(criteriaBuilder.equal(root.get("deviceBoxQrcode"), packageGetInfoRequest.getQrcode()));
                }
                if(null!=packageGetInfoRequest.getPackageCode()) {
                    predicates.add(criteriaBuilder.equal(root.get("code"), packageGetInfoRequest.getPackageCode()));
                }                
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
            
        });
        Package packageEntity = packageOpt.orElseThrow(()->new AppException("查無Package紀錄"));
        if(packageEntity.getTrackingId()==null) {
            throw new AppException("此包盤仍在申請中");
        }
        PackageInfo packageInfo = PackageInfo.builder()
            .id(packageEntity.getId())
            .configId(packageEntity.getPackageConfigId())
            .configName(packageEntity.getPackageConfig().getPackageName())
            .configCode(packageEntity.getPackageConfig().getConfigCode())
            .divisionId(packageEntity.getPackageConfig().getDivisionId())
            .qrcode(packageEntity.getDeviceBoxQrcode())
            .status(packageEntity.getStatus())
            .serialNo(packageEntity.getSerialNo())
            .packageCode(packageEntity.getCode())
            .position(packageEntity.getPosition())
            .expireTime(packageEntity.getTracking().getExpireTime()).build();
        if (packageGetInfoRequest.getDevices()){
            if (!packageEntity.getDevice().isEmpty()){
                packageInfo.setDevices(genPackageGetInfoDevices(packageEntity.getDevice()));
            }else{
                packageInfo.setDevices(new ArrayList<DeviceDetailVo>());
            }
        }
        return packageInfo;
    }
    
    private List<DeviceDetailVo> genPackageGetInfoDevices(List<Device> devices){
        List<DeviceDetailVo> result = new ArrayList<DeviceDetailVo>();
        for(Device device : devices){
            DeviceDetailVo vo = new DeviceDetailVo();
            vo.setId(device.getId());
            vo.setTypeId(device.getDeviceType().getId());
            vo.setName(device.getDeviceType().getName());
            vo.setNameScientific(device.getDeviceType().getNameScientific());
            vo.setSpec(device.getDeviceType().getSpec());
            vo.setDesc(device.getDeviceType().getDescription());
            vo.setBrand(device.getDeviceModel().getBrand());
            vo.setManufactureId(device.getDeviceModel().getManufactureId());
            vo.setStatus(device.getStatus());
            vo.setUdi(device.getUdi());
            vo.setQrcode(device.getQrcode());
            vo.setImages(device.getDeviceType().getDeviceImages().stream().map(DeviceImageVo::new).collect(Collectors.toList()));
            vo.setCost(device.getCost());
            result.add(vo);
        }
        return result;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void repack(RepackRequest request, Long accountId, Long packageId){
        Date now = new Date();
        Optional<Package> packageEntityOpt = packageRepository.findById(packageId);
        if (!packageEntityOpt.isPresent()){
            throw new AppException("查無此 packageId");
        }
        Package packageEntity = packageEntityOpt.get();
        
        if (!packageEntity.getStatus().equals(PackageStatus.RECV_FROM_CIRCU_OR_STOCK.getValue()) && !packageEntity.getStatus().equals(PackageStatus.RECV_FROM_EXPIRED_STER.getValue())){
            throw new AppException("此包盤狀態非「配盤區領取庫存(12) 或 滅菌過期領取(16)」不可執行重新打包");
        }
        Optional<DeviceBox> deviceBoxOpt = deviceBoxRepository.findByQrcode(request.getDeviceBoxQrcode());
        if (!deviceBoxOpt.isPresent()){
            throw new AppException("查無此 device box qrcode");
        }
        DeviceBox deviceBox = deviceBoxOpt.get();
        if (!deviceBox.getStatus().equals(ComponentStatusFlag.NORMAL.getValue()) && !deviceBox.getStatus().equals(ComponentStatusFlag.REPLACED.getValue())){
            throw new AppException("此 device box 狀態為 " + deviceBox.getStatus() + "，非「全新(0)」或是「待包盤 (2)」故不可執行包盤");
        }
        List<Device> oriDevices = packageEntity.getDevice();
        List<Long> oriDeviceIds = oriDevices.stream().map(device -> device.getId()).collect(Collectors.toList());
        List<Long> requestDeviceIds = new ArrayList<Long>();
        if (!request.getReplacedDevices().isEmpty()){
            requestDeviceIds.addAll(request.getReplacedDevices().stream().map(replace -> replace.getId()).collect(Collectors.toList()));
        }
        if (!request.getDevices().isEmpty()){
            requestDeviceIds.addAll(request.getDevices());
        }
        if (oriDevices.size() != requestDeviceIds.size() || !oriDeviceIds.containsAll(requestDeviceIds)){
            throw new AppException("傳入的 原deviceIds 與資料庫的不同，db的deivce ids " + oriDeviceIds.toString());
        }
        if (!oriDeviceIds.containsAll(request.getDevices())){
            throw new AppException("devices部分含有非原盤包器械，原盤包器械：" + oriDeviceIds.toString());
        }
        List<Device> changeDeviceEntity = new ArrayList<Device>();
        List<Device> requestDevices = oriDevices.stream().filter(d -> request.getDevices().contains(d.getId())).collect(Collectors.toList());
        requestDevices.forEach(device -> {
            if (!device.getStatus().equals(ComponentStatusFlag.RECEIVE_SCAN_DONE.getValue())){
                throw new AppException(device.getId() + " 器械狀態非「回收清點(3)」");
            }
            changeDeviceEntity.add(doDeviceUpdate(device, accountId, packageId, now, ComponentStatusFlag.IN_PACKAGE.getValue()));
        });
        List<DeviceCheckLog> deviceCheckLogs = new ArrayList<DeviceCheckLog>();
        List<PackageDeviceRecord> packageDeviceRecords = new ArrayList<PackageDeviceRecord>();
        for(RepackReplacedDevicesRequest replace : request.getReplacedDevices()){
            Optional<Device> oriDeviceOpt = oriDevices.stream().filter(d -> d.getId().equals(replace.getId())).findAny();
            if (!oriDeviceOpt.isPresent()){
                throw new AppException("此器械不存在 " + replace.getId());
            }
            Device oriDevice = oriDeviceOpt.get();
            if (replace.getStatus().equals(ComponentStatusFlag.REPLACED.getValue())){
                if (!oriDevice.getStatus().equals(ComponentStatusFlag.RECEIVE_SCAN_DONE.getValue()))
                    throw new AppException(oriDevice.getId() + " 目前狀態為 " + oriDevice.getStatus() + "，非「已回收清點 (3)」不可更換狀態為「待包盤」");
            }else if (replace.getStatus().equals(ComponentStatusFlag.UNSCANABLE.getValue())){
                if (!oriDevice.getStatus().equals(ComponentStatusFlag.UNSCANABLE.getValue()) && !oriDevice.getStatus().equals(ComponentStatusFlag.RECEIVE_SCAN_DONE.getValue()))
                    throw new AppException(oriDevice.getId() + " 目前狀態為 " + oriDevice.getStatus() + "，非「已回收清點 (3)」或「無法掃描(6)」，不可更換狀態為「無法掃描」");
                if (oriDevice.getStatus().equals(ComponentStatusFlag.RECEIVE_SCAN_DONE.getValue()))
                	deviceCheckLogs.add(createDeviceCheckLog(replace, packageEntity.getTrackingId(), 1, now, accountId));
            }else if (replace.getStatus().equals(ComponentStatusFlag.MISSING.getValue())){
                if (!oriDevice.getStatus().equals(ComponentStatusFlag.MISSING.getValue()) && !oriDevice.getStatus().equals(ComponentStatusFlag.RECEIVE_SCAN_DONE.getValue()))
                    throw new AppException(oriDevice.getId() + " 目前狀態為 " + oriDevice.getStatus() + "，非「已回收清點 (3)」或「遺失(7)」，不可更換狀態為「遺失」");
				if (oriDevice.getStatus().equals(ComponentStatusFlag.RECEIVE_SCAN_DONE.getValue()))
                	deviceCheckLogs.add(createDeviceCheckLog(replace, packageEntity.getTrackingId(), 1, now, accountId));
            }else{
                throw new AppException(replace.getId() + " 傳入的status 不符流程");
            }
            Optional<Device> newDeviceOpt = deviceRepository.findById(replace.getNewDeviceId());
            if (!newDeviceOpt.isPresent()){
                throw new AppException(replace.getNewDeviceId() + " 查無此器械");
            }
            Device newDevice = newDeviceOpt.get();
            if (!newDevice.getStatus().equals(ComponentStatusFlag.NORMAL.getValue()) && !newDevice.getStatus().equals(ComponentStatusFlag.REPLACED.getValue())){
                throw new AppException(newDevice.getId() + " 此器械狀態為 " + newDevice.getStatus() + "，非「全新(0)」或是「待包盤(2)」不可包盤");
            }
            if (!newDevice.getTypeId().equals(oriDevice.getTypeId())){
                throw new AppException(newDevice.getId() + " 此器械與" + oriDevice.getId() + " 器械的typeId不同，不可替換");
            }
            if (!newDevice.getDivision().equals(packageEntity.getPackageConfig().getDivisionId().intValue())){
                throw new AppException(newDevice.getId() + " 此器械與 此盤包的科別不同，不可替換");
            }
            changeDeviceEntity.add(doDeviceUpdate(oriDevice, accountId, null, now, replace.getStatus()));
            changeDeviceEntity.add(doDeviceUpdate(newDevice, accountId, packageId, now, ComponentStatusFlag.IN_PACKAGE.getValue()));
            Optional<PackageDeviceRecord> oriPackageDeviceRecordOpt = packageDeviceRecordRepository.findByPackageIdAndDeviceIdAndDeviceOutTimeIsNull(packageId, replace.getId());
            if (oriPackageDeviceRecordOpt.isPresent()){
                PackageDeviceRecord oriPackageDeviceRecord = oriPackageDeviceRecordOpt.get();
                oriPackageDeviceRecord.setUpdater(accountId);
                oriPackageDeviceRecord.setDeviceOutTime(now);
                packageDeviceRecords.add(oriPackageDeviceRecord);
            }
            PackageDeviceRecord newPackageDeviceRecord = new PackageDeviceRecord();
            newPackageDeviceRecord.setCreator(accountId);
            newPackageDeviceRecord.setDeviceId(newDevice.getId());
            newPackageDeviceRecord.setDeviceInTime(now);
            newPackageDeviceRecord.setPackageId(packageId);
            newPackageDeviceRecord.setUpdater(accountId);
            packageDeviceRecords.add(newPackageDeviceRecord);
        }
        List<Tracking> trackings = new ArrayList<Tracking>();
        Tracking oriTracking = packageEntity.getTracking();
        oriTracking.setFinishTime(now);
        trackings.add(oriTracking);
        Tracking tracking = new Tracking();
        tracking.setPackageId(packageId);
        tracking.setCreateTime(now);
        trackings.add(tracking);
        trackingRepository.saveAll(trackings);
        TrackingRecord trackingRecord = new TrackingRecord(tracking.getId(), PackageStatus.PACK_DONE.getValue(), now, accountId);
        trackingRecordRepository.save(trackingRecord);
        deviceBox.setUpdater(accountId);
        deviceBox.setUpdateTime(now);
        deviceBox.setStatus(ComponentStatusFlag.IN_PACKAGE.getValue());
        if (null == deviceBox.getUsedTime()){
            deviceBox.setUsedTime(now);
        }
        deviceBoxRepository.save(deviceBox);
        deviceCheckLogRepository.saveAll(deviceCheckLogs);
        packageEntity.setStatus(PackageStatus.PACK_DONE.getValue());
        packageEntity.setDeviceBoxQrcode(deviceBox.getQrcode());
        packageEntity.setTrackingId(tracking.getId());
        deviceRepository.saveAll(changeDeviceEntity);
        packageDeviceRecordRepository.saveAll(packageDeviceRecords);
        packageRepository.save(packageEntity);
    }

    private Device doDeviceUpdate(Device device, Long accountId, Long packageId, Date now, Integer status){
        device.setStatus(status);
        device.setUpdater(accountId);
        device.setUpdateTime(now);
        device.setPackageId(packageId);
        if(null == device.getUsedTime()) {
            device.setUsedTime(now);
        }
        return device;
    }
    
    private DeviceCheckLog createDeviceCheckLog(RepackReplacedDevicesRequest request, Long trackingId, Integer type, Date now, Long accountId) {
        DeviceCheckLog deviceCheckLog = new DeviceCheckLog();
        deviceCheckLog.setDeviceId(request.getId());
        deviceCheckLog.setStatus(request.getStatus());
        deviceCheckLog.setTrackingId(trackingId);
        deviceCheckLog.setType(type);
        deviceCheckLog.setCreateTime(now);
        deviceCheckLog.setCreator(accountId);
        return deviceCheckLog;
    }
    
    public PackageDashboardResponseVo dashboard(){
        PackageDashboardResponseVo packageDashboardResponse = new PackageDashboardResponseVo();
        
        Long packingQty = packageRepository.countByStatusIn(Arrays.asList(PackageStatus.PACK_DONE.getValue(), PackageStatus.PACK_TO_STER.getValue(), PackageStatus.RECV_FROM_CIRCU_OR_STOCK.getValue()));
        Long sterilizationQty = packageRepository.countByStatusIn(Arrays.asList(PackageStatus.STER_RECV.getValue(), PackageStatus.STER_PROCESS.getValue(), PackageStatus.STER_DONE.getValue(), PackageStatus.STER_TO_STOCK.getValue(), PackageStatus.STER_FAIL.getValue()));
        Long circulationQty = packageRepository.countByStatusIn(Arrays.asList(PackageStatus.CIRCULATION.getValue(), PackageStatus.CIRCU_TO_PACK.getValue(), PackageStatus.WASH_FAIL.getValue()));
        Long supplyQty = packageRepository.countByStatusIn(Arrays.asList(PackageStatus.IN_STOCK.getValue()));
        Long availableQty = packageRepository.countAvailableQty();
        Long expiredQty = packageRepository.countExpiredQty();
        Long reSterilizeQty = packageRepository.countByStatusIn(Arrays.asList(PackageStatus.RE_STERILIZE.getValue()));
        Long usedQty = packageRepository.countByStatusIn(Arrays.asList(PackageStatus.DISTRIBUTE.getValue(), PackageStatus.IN_USE.getValue(), PackageStatus.USE_DONE.getValue()));
        
        StationsVo stations = new StationsVo();
        stations.setPacking(packingQty);
        stations.setSterilization(sterilizationQty);
        stations.setCirculation(circulationQty);
        stations.setSupply(supplyQty + reSterilizeQty);
        packageDashboardResponse.setStations(stations);
        
        StatusVo status = new StatusVo();
        status.setAvailable(availableQty);
        status.setExpired(expiredQty);
        status.setProcess(packingQty + sterilizationQty + circulationQty + reSterilizeQty);
        status.setUsed(usedQty);
        packageDashboardResponse.setStatus(status);
        
        List<DivisionPackagesVo> divisionPackages = packageRepository.getDivisionPackagesByStatusNotIn(Arrays.asList(PackageStatus.APPLY.getValue(), PackageStatus.UNPACK.getValue()));
        packageDashboardResponse.setDivisionPackages(divisionPackages);
        return packageDashboardResponse;
    }
    
    public Page<ReportPackageHistoryListResponse> historyList(ReportPackageHistoryListRequest request) throws ParseException{
        if (( request.getStart() == null && request.getEnd() != null ) || ( request.getStart() != null && request.getEnd() == null )){
            throw new AppException("有 start 就必須有 end");
        }
        Pageable pageable = PageRequest.of(request.getPage() == null ? 0 : request.getPage(), 10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = null;
        Date end = null;
        Date sterilizationDateStart = null;
        Date sterilizationDateEnd = null;
        if (request.getStart() != null && !request.getStart().isEmpty()){
            start = sdf.parse(request.getStart() + " 00:00:00");
            end = sdf.parse(request.getEnd() + " 23:59:59");
            if (end.before(start)){
                throw new AppException("end cannot be smaller then start");
            }
        }
        if (request.getSterilizationDate() != null && !request.getSterilizationDate().isEmpty()){
            sterilizationDateStart = sdf.parse(request.getSterilizationDate() + " 00:00:00");
            sterilizationDateEnd = sdf.parse(request.getSterilizationDate() + " 23:59:59");
        }
        Page<ReportPackageHistoryListResponse> response = trackingRepository.queryForPackageHistoryList(start, end, request.getConfigCode(), request.getSerialNo(), request.getSterilizer(), request.getMedicalRecordNo(), sterilizationDateStart, sterilizationDateEnd, pageable);
        return response;
    }
    
    public Page<PackageExpiredListResponse> expiredList(PackageExpiredListRequest packageExpiredListRequest) throws ParseException{
        Pageable pageable = PageRequest.of(packageExpiredListRequest.getPage(), 10);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = null;
        Date end = null;
        if (StringUtils.isNoneBlank(packageExpiredListRequest.getStart(), packageExpiredListRequest.getEnd())){
            start = simpleDateFormat.parse(packageExpiredListRequest.getStart() + " 00:00:00");
            end = simpleDateFormat.parse(packageExpiredListRequest.getEnd() + " 23:59:59");
            if (end.before(start)){
                throw new AppException("結束時間(end)不能小於開始時間(start)");
            }
        }else if (null == packageExpiredListRequest.getStart() && null != packageExpiredListRequest.getEnd()){
            throw new AppException("開始時間(start)不可為空");
        }else if (null != packageExpiredListRequest.getStart() && null == packageExpiredListRequest.getEnd()){
            throw new AppException("結束時間(end)不可為空");
        }
        return trackingRepository.queryPackageExpiredList(PackageStatus.IN_STOCK.getValue(), start, end, pageable);
    }
    
    public List<PackageTrackingDeviceListVo> trackingDeviceList(Long trackingId){
        Optional<Tracking> trackingOpt = trackingRepository.findById(trackingId);
        Tracking tracking = trackingOpt.orElseThrow(() -> new AppException("查無追蹤紀錄"));
        Optional<List<PackageDeviceRecord>> packageDeviceRecordsOpt = packageDeviceRecordRepository.queryPackageDeviceRecords(tracking.getPackageId(), tracking.getCreateTime());
        List<PackageDeviceRecord> packageDeviceRecords = packageDeviceRecordsOpt.orElseThrow(() -> new AppException("查無對應器械紀錄"));
        List<PackageTrackingDeviceListVo> packageTrackingDeviceListVos = new ArrayList<PackageTrackingDeviceListVo>();
        for(PackageDeviceRecord packageDeviceRecord : packageDeviceRecords){
            Device device = packageDeviceRecord.getDevice();
            DeviceType deviceType = device.getDeviceType();
            DeviceModel deviceModel = device.getDeviceModel();
            List<DeviceImage> deviceImages = deviceType.getDeviceImages();
            PackageTrackingDeviceListVo packageTrackingDeviceListVo = new PackageTrackingDeviceListVo();
            packageTrackingDeviceListVo.setId(packageDeviceRecord.getId());
            packageTrackingDeviceListVo.setTypeId(device.getTypeId());
            packageTrackingDeviceListVo.setName(deviceType.getName());
            packageTrackingDeviceListVo.setNameScientific(deviceType.getNameScientific());
            packageTrackingDeviceListVo.setSpec(deviceType.getSpec());
            packageTrackingDeviceListVo.setDesc(deviceType.getDescription());
            packageTrackingDeviceListVo.setBrand(deviceModel.getBrand());
            packageTrackingDeviceListVo.setManufactureId(deviceModel.getManufactureId());
            packageTrackingDeviceListVo.setStatus(device.getStatus());
            packageTrackingDeviceListVo.setUdi(device.getUdi());
            packageTrackingDeviceListVo.setQrcode(device.getQrcode());
            packageTrackingDeviceListVo.setCost(device.getCost());
            packageTrackingDeviceListVo.setImages(deviceImages.stream().filter(di -> di.getIsMain()).map(DeviceImageVo::new).collect(Collectors.toList()));
            packageTrackingDeviceListVos.add(packageTrackingDeviceListVo);
        }
        return packageTrackingDeviceListVos;
    }
    
    public PackageTrackingResponse tracking(Long trackingId) {
        PackageTrackingResponse response = trackingRepository.queryForPackageTracking(trackingId);
        if(response == null) {
            throw new AppException("查無此 tracking id");
        }
        response.setTrackingRecords(trackingRecordRepository.queryTrackingRecordInfo(trackingId));
        return response;
    }
}