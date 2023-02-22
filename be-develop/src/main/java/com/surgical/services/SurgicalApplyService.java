package com.surgical.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surgical.entities.Package;
import com.surgical.entities.PackageOrder;
import com.surgical.entities.PackageOrderDetail;
import com.surgical.entities.PackageOrderMapping;
import com.surgical.entities.SurgicalApply;
import com.surgical.entities.TrackingRecord;
import com.surgical.enums.PackageOrderMappingStatus;
import com.surgical.enums.PackageOrderStatus;
import com.surgical.enums.PackageStatus;
import com.surgical.enums.SurgicalApplyStatus;
import com.surgical.enums.SurgicalOrderStatus;
import com.surgical.exception.AppException;
import com.surgical.repositories.PackageConfigRepository;
import com.surgical.repositories.PackageOrderDetailRepository;
import com.surgical.repositories.PackageOrderMappingRepository;
import com.surgical.repositories.PackageOrderRepository;
import com.surgical.repositories.PackageRepository;
import com.surgical.repositories.SurgicalApplyRepository;
import com.surgical.repositories.TrackingRecordRepository;
import com.surgical.utils.DateUtils;
import com.surgical.vo.OrderDetailVo;
import com.surgical.vo.OrderVo;
import com.surgical.vo.PackageInfo;
import com.surgical.vo.PackageOrderCreateRequest;
import com.surgical.vo.PackageOrderInfoListVo;
import com.surgical.vo.SurgicalApplyCreateOrdersRequest;
import com.surgical.vo.SurgicalApplyCreateRequest;
import com.surgical.vo.SurgicalApplyDto;
import com.surgical.vo.SurgicalApplyGrantDetailInfoVo;
import com.surgical.vo.SurgicalApplyGrantProcessRequest;
import com.surgical.vo.SurgicalApplyGrantProcessRequestOrderPart;

@Service
public class SurgicalApplyService{

    @Autowired
    private SurgicalApplyRepository surgicalApplyRepository;
    
    @Autowired
    private PackageOrderRepository packageOrderRepository;
    
    @Autowired
    private PackageRepository packageRepository;
    
    @Autowired
    private TrackingRecordRepository trackingRecordRepository;
    
    @Autowired
    private PackageOrderMappingRepository packageOrderMappingRepository;
    
    @Autowired
    private PackageOrderDetailRepository packageOrderDetailRepository;
    
    @Autowired
    private PackageConfigRepository packageConfigRepository;

    @Transactional(rollbackFor = Exception.class)
    public void grantProcess(SurgicalApplyGrantProcessRequest request, Long accountId){
        Optional<PackageOrder> orderOpt = packageOrderRepository.findById(request.getOrderId());
        if (!orderOpt.isPresent()){
            throw new AppException("查無此訂單");
        }
        PackageOrder order = orderOpt.get();
        if (order.getStatus() != SurgicalOrderStatus.YET_RECEIVED.getValue()){
            throw new AppException("此訂單狀態非「待發放」");
        }
        SurgicalApply surgicalApply = order.getSurgicalApply();
        if (surgicalApply.getStatus() != SurgicalApplyStatus.REVIEW.getValue() && surgicalApply.getStatus() != SurgicalApplyStatus.USING.getValue() && surgicalApply.getStatus() != SurgicalApplyStatus.RECEIVED.getValue()){
            throw new AppException("此手術器械申請單狀態非「已審核」、「已領用」、「使用中」不可執行發放");
        }
        List<Package> packagesForUpdate = new ArrayList<Package>();
        List<TrackingRecord> trackingRecords = new ArrayList<TrackingRecord>();
        List<PackageOrderMapping> packageOrderMappings = new ArrayList<PackageOrderMapping>();
        List<PackageOrderDetail> orderDetails = order.getPackageOrderDetails();
        Date now = new Date();
        if (request.getOrders().size() != orderDetails.size()){
            throw new AppException("傳入需求與訂單需求不符");
        }
        for(SurgicalApplyGrantProcessRequestOrderPart requestOrder : request.getOrders()){
            Optional<PackageOrderDetail> existedOrderOptional = orderDetails.stream().filter(detail -> detail.getPackageConfigId().equals(requestOrder.getConfigId()) && detail.getAmount().equals(requestOrder.getPackages().size())).findAny();
            if (!existedOrderOptional.isPresent()){
                throw new AppException("輸入 盤種及數量 與訂單需求不符");
            }
            List<Package> packages = packageRepository.findByPackageConfigIdAndIdIn(requestOrder.getConfigId(), requestOrder.getPackages());
            if (packages.size() != requestOrder.getPackages().size()){
                throw new AppException("傳入非此盤種之包盤");
            }
            for(Package packageEntity : packages){
                if (!packageEntity.getStatus().equals(PackageStatus.IN_STOCK.getValue())){
                    throw new AppException("包盤狀態錯誤" + packageEntity.getDeviceBoxQrcode());
                }
                if (packageEntity.getTracking().getExpireTime().before(now)){
                    throw new AppException("包盤滅菌逾期" + packageEntity.getDeviceBoxQrcode());
                }
                packageEntity.setStatus(PackageStatus.DISTRIBUTE.getValue());
                packageEntity.setUsedCount(packageEntity.getUsedCount() + 1);
                packageEntity.getDeviceBox().setUsedCount(packageEntity.getDeviceBox().getUsedCount() + 1);
                packageEntity.getTracking().setUseTime(now);
                packagesForUpdate.add(packageEntity);
                TrackingRecord trackingRecord = new TrackingRecord();
                trackingRecord.setTrackingId(packageEntity.getTrackingId());
                trackingRecord.setStatus(PackageStatus.DISTRIBUTE.getValue());
                trackingRecord.setCreateTime(now);
                trackingRecord.setCreator(accountId);
                trackingRecords.add(trackingRecord);
                PackageOrderMapping orderMapping = new PackageOrderMapping();
                orderMapping.setPackageOrderId(order.getId());
                orderMapping.setPackageId(packageEntity.getId());
                orderMapping.setTrackingId(packageEntity.getTrackingId());
                orderMapping.setStatus(PackageOrderMappingStatus.NOT_CHECK.getValue()); 
                packageOrderMappings.add(orderMapping);
            }
        }
        if(surgicalApply.getStatus() == SurgicalApplyStatus.REVIEW.getValue()) {
            surgicalApply.setStatus(SurgicalApplyStatus.RECEIVED.getValue()); 
            surgicalApply.setUpdater(accountId);
            surgicalApply.setUpdateTime(now);
        }
        order.setStatus(PackageOrderStatus.COLLECTED.getValue()); 
        surgicalApplyRepository.save(surgicalApply);
        packageOrderRepository.save(order);
        packageRepository.saveAll(packagesForUpdate);
        trackingRecordRepository.saveAll(trackingRecords);
        packageOrderMappingRepository.saveAll(packageOrderMappings);
    }
    
    public SurgicalApplyGrantDetailInfoVo getSurgicalApplyDetail(SurgicalApplyDto surgicalApplyDto){
        if (surgicalApplyDto.getApplyId() == null && surgicalApplyDto.getTrackingId() == null){
            throw new AppException("請輸入 applyId 或 trackingId");
        }
        SurgicalApply surgicalApply;
        if (surgicalApplyDto.getTrackingId() != null){
            Optional<PackageOrderMapping> packageOrderMappingOpt = packageOrderMappingRepository.findByTrackingId(surgicalApplyDto.getTrackingId());
            PackageOrderMapping packageOrderMapping = packageOrderMappingOpt.orElseThrow(() -> new AppException("查無手術申請紀錄"));
            surgicalApply = packageOrderMapping.getPackageOrder().getSurgicalApply();
        }else{
            Optional<SurgicalApply> surgicalApplyOpt = surgicalApplyRepository.findById(surgicalApplyDto.getApplyId());
            surgicalApply = surgicalApplyOpt.orElseThrow(() -> new AppException("查無手術申請紀錄"));
        }
        List<PackageOrder> orders = surgicalApply.getOrders();
        if (orders == null){
            throw new AppException("查無手術申請項目");
        }
        SurgicalApplyGrantDetailInfoVo surgicalApplyGrantDetailInfoVo = genSurgicalApplyDetailInfo(surgicalApply);
        surgicalApplyGrantDetailInfoVo.setOrders(genOrderVos(orders, surgicalApplyDto));
        return surgicalApplyGrantDetailInfoVo;
    }
    
    private List<OrderVo> genOrderVos(List<PackageOrder> orders, SurgicalApplyDto dto){
        List<OrderVo> result = new ArrayList<OrderVo>();
        for(PackageOrder order : orders) {
            List<PackageInfo> packages = new ArrayList<PackageInfo>();
            List<OrderDetailVo> details = new ArrayList<OrderDetailVo>();
            if(dto.getCount()) {
                if(order.getStatus().equals(PackageOrderStatus.NOT_COLLECT.getValue())) {
                    details = genOrderDetailsVo(order.getId());
                }else {
                    packages = genPackageInfoVos(order.getId());
                }
            }else {
                details = genOrderDetailsVo(order.getId());
            }
            OrderVo orderVo = OrderVo.builder()
                .id(order.getId())
                .status(order.getStatus())
                .details(details.isEmpty()?null:details)
                .packages(packages.isEmpty()?null:packages)
                .build();
            result.add(orderVo);
        }
        return result;
    }
    
    private List<OrderDetailVo> genOrderDetailsVo(Long orderId){
        List<OrderDetailVo> result = new ArrayList<OrderDetailVo>();
        List<PackageOrderDetail> packageOrderDetails = packageOrderDetailRepository.findByPackageOrderId(orderId);
        if(packageOrderDetails==null) {
            return null;
        }
        for(PackageOrderDetail packageOrderDetail : packageOrderDetails) {
            OrderDetailVo orderDetailVo = new OrderDetailVo();
            orderDetailVo.setConfigId(packageOrderDetail.getPackageConfigId());
            orderDetailVo.setConfigCode(packageOrderDetail.getPackageConfig().getConfigCode());
            orderDetailVo.setConfigDivisionId(packageOrderDetail.getPackageConfig().getDivisionId());
            orderDetailVo.setConfigName(packageOrderDetail.getPackageConfig().getPackageName());
            orderDetailVo.setQty(packageOrderDetail.getAmount());
            result.add(orderDetailVo);
        }
        return result;
    }
    
    private List<PackageInfo> genPackageInfoVos(Long orderId){
        List<PackageInfo> result = new  ArrayList<PackageInfo>();
        List<PackageOrderMapping> poMappings = packageOrderMappingRepository.findByPackageOrderId(orderId);
        if(poMappings==null) {
            return result;
        }
        for(PackageOrderMapping pom:poMappings) {
            PackageInfo packageInfo = PackageInfo.builder()
                .id(pom.getPackageId())
                .orderMappingId(pom.getId())
                .qrcode(pom.getPack().getDeviceBoxQrcode())
                .configId(pom.getPack().getPackageConfigId())
                .configCode(pom.getPack().getPackageConfig().getConfigCode())
                .configName(pom.getPack().getPackageConfig().getPackageName())
                .divisionId(pom.getPack().getPackageConfig().getDivisionId())
                .trackingId(pom.getPack().getTrackingId())
                .status(pom.getPack().getStatus()).build();
            result.add(packageInfo);
        }
        return result;
    }
    
    private SurgicalApplyGrantDetailInfoVo genSurgicalApplyDetailInfo(SurgicalApply surgicalApply) {
        return SurgicalApplyGrantDetailInfoVo.builder()
            .applyId(surgicalApply.getId())
            .operatingNumber(surgicalApply.getOperatingNumber())
            .divisionId(surgicalApply.getDivisionId())
            .status(surgicalApply.getStatus())
            .medicalRecordNumber(surgicalApply.getMedicalRecordNumber())
            .surgeryName(surgicalApply.getName())
            .doctor(surgicalApply.getDoctor())
            .operatingRoom(surgicalApply.getOperatingRoom())
            .operatingDate(DateUtils.getDateInString("yyyy-MM-dd", surgicalApply.getOperatingDate()))
            .operatingOrder(surgicalApply.getOperatingOrder())
            .createTime(surgicalApply.getCreateTime())
            .build();
    }
    
    public Page<PackageOrderInfoListVo> orderList(Integer status, String sort, Pageable page){
        Specification<PackageOrder> specification = new Specification<PackageOrder>(){

            public Predicate toPredicate(Root<PackageOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder){
                List<Predicate> predicates = new ArrayList<Predicate>();
                Join<PackageOrder,SurgicalApply> surgicalApplyJoin = root.join("surgicalApply",JoinType.LEFT);
                if (status != null){
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                if ("ASC".equalsIgnoreCase(sort)){
                    Order asc = criteriaBuilder.asc(surgicalApplyJoin.get("operatingDate"));
                    criteriaQuery.orderBy(asc);
                }else{
                    Order desc = criteriaBuilder.desc(surgicalApplyJoin.get("operatingDate"));
                    criteriaQuery.orderBy(desc);
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        Page<PackageOrder> packageOrder = packageOrderRepository.findAll(specification, page);
        Page<PackageOrderInfoListVo> packageOrderInfoListVo = packageOrder.map(new Function<PackageOrder, PackageOrderInfoListVo>(){

            @Override
            public PackageOrderInfoListVo apply(PackageOrder packageOrder){
                PackageOrderInfoListVo packageOrderInfo = new PackageOrderInfoListVo();
                SurgicalApply surgicalApply = packageOrder.getSurgicalApply();
                packageOrderInfo.setApplyId(packageOrder.getSurgicalApplyId());
                packageOrderInfo.setOrderId(packageOrder.getId());
                packageOrderInfo.setOperatingNumber(surgicalApply.getOperatingNumber());
                packageOrderInfo.setDivisionId(surgicalApply.getDivisionId());
                packageOrderInfo.setStatus(packageOrder.getStatus());
                packageOrderInfo.setMedicalRecordNumber(surgicalApply.getMedicalRecordNumber());
                packageOrderInfo.setSurgeryName(surgicalApply.getName());
                packageOrderInfo.setDoctor(surgicalApply.getDoctor());
                packageOrderInfo.setOperatingRoom(surgicalApply.getOperatingRoom());
                packageOrderInfo.setOperatingDate(DateUtils.getDateInString("yyyy-MM-dd", surgicalApply.getOperatingDate()));
                packageOrderInfo.setOperatingOrder(surgicalApply.getOperatingOrder());
                packageOrderInfo.setCreateTime(packageOrder.getCreateTime());
                return packageOrderInfo;
            }
        });
        return packageOrderInfoListVo;
    }
    
    public Page<PackageOrderInfoListVo> getList(String qrcode, List<Integer> statuses, Pageable page){
        if (StringUtils.isNotBlank(qrcode)){
            Page<Package> packagePage = packageRepository.findByDeviceBoxQrcode(qrcode, page);
            if (packagePage.getContent().isEmpty()){
                throw new AppException("qrcode查無對應包盤資料");
            }
            return packgeTransToVo(packagePage, statuses);
        }else{
            if (null != statuses && !statuses.isEmpty()){
                Page<SurgicalApply> surgicalApplyPage = surgicalApplyRepository.findByStatusInOrderByOperatingDate(statuses, page);
                return surgicalApplyTransToVo(surgicalApplyPage);
            }else{
                Page<SurgicalApply> surgicalApplyPage = surgicalApplyRepository.findAllByOrderByOperatingDate(page);
                return surgicalApplyTransToVo(surgicalApplyPage);
            }
        }
    }

    private Page<PackageOrderInfoListVo> packgeTransToVo(Page<Package> packagePage, List<Integer> statuses){
        Page<PackageOrderInfoListVo> packageOrderInfoListVo = packagePage.map(new Function<Package, PackageOrderInfoListVo>(){

            @Override
            public PackageOrderInfoListVo apply(Package packageInfo){
                PackageOrderInfoListVo surgicalApplyInfo = new PackageOrderInfoListVo();
                SurgicalApply surgicalApply = new SurgicalApply();
                try{
                    surgicalApply = packageInfo.getPackageOrderMapping().getPackageOrder().getSurgicalApply();
                }catch(Exception e){
                    throw new AppException("包盤對應資料錯誤");
                }
                if (null == statuses || statuses.isEmpty() || ( null != statuses && statuses.contains(surgicalApply.getStatus()) )){
                    surgicalApplyInfo.setApplyId(surgicalApply.getId());
                    surgicalApplyInfo.setOperatingNumber(surgicalApply.getOperatingNumber());
                    surgicalApplyInfo.setDivisionId(surgicalApply.getDivisionId());
                    surgicalApplyInfo.setStatus(surgicalApply.getStatus());
                    surgicalApplyInfo.setMedicalRecordNumber(surgicalApply.getMedicalRecordNumber());
                    surgicalApplyInfo.setSurgeryName(surgicalApply.getName());
                    surgicalApplyInfo.setDoctor(surgicalApply.getDoctor());
                    surgicalApplyInfo.setOperatingRoom(surgicalApply.getOperatingRoom());
                    surgicalApplyInfo.setOperatingDate(DateUtils.getDateInString("yyyy-MM-dd", surgicalApply.getOperatingDate()));
                    surgicalApplyInfo.setOperatingOrder(surgicalApply.getOperatingOrder());
                    surgicalApplyInfo.setCreateTime(surgicalApply.getCreateTime());
                    return surgicalApplyInfo;
                }else {
                    throw new AppException("qrcode對應 手術申請狀態 與 輸入狀態 不符!");
                }
                
            }
        });
        if(packageOrderInfoListVo.getContent().size()>1) {
            Collections.sort(packageOrderInfoListVo.getContent(), new Comparator<PackageOrderInfoListVo>(){

                @Override
                public int compare(PackageOrderInfoListVo before, PackageOrderInfoListVo after){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Long date1 = 0L, date2 = 0L;
                    try{
                        date1 = format.parse(before.getOperatingDate()).getTime();
                        date2 = format.parse(after.getOperatingDate()).getTime();
                    }catch(ParseException e){
                        e.printStackTrace();
                    }
                    return date2.compareTo(date1);
                }
            });  
        }
        return packageOrderInfoListVo;
    }

    public Page<PackageOrderInfoListVo> surgicalApplyTransToVo(Page<SurgicalApply> surgicalApplyPage){
        Page<PackageOrderInfoListVo> packageOrderInfoListVo = surgicalApplyPage.map(new Function<SurgicalApply, PackageOrderInfoListVo>(){

            @Override
            public PackageOrderInfoListVo apply(SurgicalApply surgicalApply){
                PackageOrderInfoListVo surgicalApplyInfo = new PackageOrderInfoListVo();
                surgicalApplyInfo.setApplyId(surgicalApply.getId());
                surgicalApplyInfo.setOperatingNumber(surgicalApply.getOperatingNumber());
                surgicalApplyInfo.setDivisionId(surgicalApply.getDivisionId());
                surgicalApplyInfo.setStatus(surgicalApply.getStatus());
                surgicalApplyInfo.setMedicalRecordNumber(surgicalApply.getMedicalRecordNumber());
                surgicalApplyInfo.setSurgeryName(surgicalApply.getName());
                surgicalApplyInfo.setDoctor(surgicalApply.getDoctor());
                surgicalApplyInfo.setOperatingRoom(surgicalApply.getOperatingRoom());
                surgicalApplyInfo.setOperatingDate(DateUtils.getDateInString("yyyy-MM-dd", surgicalApply.getOperatingDate()));
                surgicalApplyInfo.setOperatingOrder(surgicalApply.getOperatingOrder());
                surgicalApplyInfo.setCreateTime(surgicalApply.getCreateTime());
                return surgicalApplyInfo;
            }
        });
        return packageOrderInfoListVo;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void create(SurgicalApplyCreateRequest request, Long accountId){
        Date now = new Date();
        List<Long> configIds = request.getOrders().stream().map(x -> x.getConfigId()).collect(Collectors.toList());
        if( packageConfigRepository.findByIdIn(configIds).size() != configIds.size()) {
            throw new AppException("輸入 config 有誤");
        }
        SurgicalApply surgicalApply = new SurgicalApply();
        surgicalApply.setDivisionId(request.getDivisionId());
        surgicalApply.setOperatingRoom(request.getOperatingRoom());
        surgicalApply.setStatus(SurgicalApplyStatus.REVIEW.getValue());
        surgicalApply.setMedicalRecordNumber(request.getMedicalRecordNumber());
        surgicalApply.setName(request.getSurgeryName());
        surgicalApply.setDoctor(request.getDoctor());
        surgicalApply.setOperatingDate(request.getOperatingDate());
        surgicalApply.setOperatingOrder(request.getOperatingOrder());
        surgicalApply.setOperatingNumber(request.getOperatingNumber());
        surgicalApply.setCreateTime(now);
        surgicalApply.setUpdateTime(now);
        surgicalApply.setUpdater(accountId);
        surgicalApply.setCreator(accountId);
        surgicalApplyRepository.save(surgicalApply);
        PackageOrder packageOrder = new PackageOrder();
        packageOrder.setSurgicalApplyId(surgicalApply.getId());
        packageOrder.setStatus(SurgicalOrderStatus.YET_RECEIVED.getValue());
        packageOrder.setCreateTime(now);
        packageOrderRepository.save(packageOrder);
        List<PackageOrderDetail> details = new ArrayList<PackageOrderDetail>();
        for(SurgicalApplyCreateOrdersRequest ordersRequest : request.getOrders()){
            PackageOrderDetail packageOrderDetail = new PackageOrderDetail();
            packageOrderDetail.setPackageOrderId(packageOrder.getId());
            packageOrderDetail.setPackageConfigId(ordersRequest.getConfigId());
            packageOrderDetail.setAmount(ordersRequest.getQty());
            details.add(packageOrderDetail);
        }
        packageOrderDetailRepository.saveAll(details);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void orderCancel(Long orderId, Long accountId){
        Optional<PackageOrder> orderOpt = packageOrderRepository.findById(orderId);
        PackageOrder packageOrder = orderOpt.orElseThrow(() -> new AppException("查無此訂單"));
        PackageOrderStatus packageOrderStatus = PackageOrderStatus.convertByValue(packageOrder.getStatus());
        if (packageOrderStatus == null) {
            throw new AppException("此訂單狀態異常");
        }
        else if (packageOrderStatus != PackageOrderStatus.NOT_COLLECT){
            throw new AppException(String.format("取消手術訂單失敗，訂單 #%d 狀態為 %d:%s", orderId, packageOrderStatus.getValue(), packageOrderStatus.getComment()));
        }
        packageOrder.setStatus(PackageOrderStatus.CANCEL_ORDER.getValue());
        packageOrderRepository.save(packageOrder);
        SurgicalApply surgicalApply = packageOrder.getSurgicalApply();
        Supplier<Stream<PackageOrder>> ordersStreamSupplier = () -> surgicalApply.getOrders().stream();
        if (ordersStreamSupplier.get().allMatch(po -> po.getStatus() == PackageOrderStatus.CANCEL_ORDER.getValue())){
            surgicalApply.setStatus(SurgicalApplyStatus.CANCEL_APPLY.getValue());
            surgicalApply.setUpdater(accountId);
            surgicalApply.setUpdateTime(new Date());
            surgicalApplyRepository.save(surgicalApply);
        }else if (ordersStreamSupplier.get().allMatch(po -> po.getStatus() == PackageOrderStatus.IN_USE.getValue() || po.getStatus() == PackageOrderStatus.FINISH.getValue() || po.getStatus() == PackageOrderStatus.CANCEL_ORDER.getValue())){
            surgicalApply.setStatus(SurgicalApplyStatus.USING.getValue());
            surgicalApply.setUpdater(accountId);
            surgicalApply.setUpdateTime(new Date());
            surgicalApplyRepository.save(surgicalApply);
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void packageOrderCreate(PackageOrderCreateRequest packageOrderCreate) {
    	Optional<SurgicalApply> surgicalApplyOpt = surgicalApplyRepository.findById(packageOrderCreate.getSurgicalApplyId());
    	if(surgicalApplyOpt.isPresent()) {
    		SurgicalApply surgicalApply = surgicalApplyOpt.get();
    		if(!SurgicalApplyStatus.RECEIVED.getValue().equals(surgicalApply.getStatus()) && !SurgicalApplyStatus.USING.getValue().equals(surgicalApply.getStatus()))
    			throw new AppException("此訂單狀態不符");
    		List<Long> configIds = packageOrderCreate.getOrders().stream().map(x -> x.getConfigId()).collect(Collectors.toList());
            if( packageConfigRepository.findByIdIn(configIds).size() != configIds.size()) {
                throw new AppException("輸入 config 有誤");
            }
            //若補申請器械則需要將SurgicalApply的狀態調整回“已發放
            if(SurgicalApplyStatus.USING.getValue().equals(surgicalApply.getStatus())) {
            	surgicalApply.setStatus(SurgicalApplyStatus.RECEIVED.getValue());
            	surgicalApplyRepository.save(surgicalApply);
            }
            PackageOrder packageOrder = new PackageOrder();
            packageOrder.setSurgicalApplyId(surgicalApply.getId());
            packageOrder.setStatus(SurgicalOrderStatus.YET_RECEIVED.getValue());
            packageOrder.setCreateTime(new Date());
            packageOrderRepository.save(packageOrder);
            List<PackageOrderDetail> details = new ArrayList<PackageOrderDetail>();
            for(SurgicalApplyCreateOrdersRequest ordersRequest : packageOrderCreate.getOrders()){
                PackageOrderDetail packageOrderDetail = new PackageOrderDetail();
                packageOrderDetail.setPackageOrderId(packageOrder.getId());
                packageOrderDetail.setPackageConfigId(ordersRequest.getConfigId());
                packageOrderDetail.setAmount(ordersRequest.getQty());
                details.add(packageOrderDetail);
            }
            packageOrderDetailRepository.saveAll(details);
    	}
    }  
}
