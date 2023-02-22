package com.surgical.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surgical.entities.PackageOrder;
import com.surgical.entities.PackageOrderMapping;
import com.surgical.entities.SurgicalApply;
import com.surgical.enums.PackageOrderMappingStatus;
import com.surgical.enums.PackageOrderStatus;
import com.surgical.enums.SurgicalApplyStatus;
import com.surgical.exception.AppException;
import com.surgical.repositories.PackageOrderRepository;
import com.surgical.repositories.SurgicalApplyRepository;

@Service
public class PackageOrderService{

    private static final Logger logger = LoggerFactory.getLogger(PackageOrderService.class);

    @Autowired
    PackageOrderRepository packageOrderRepository;
    
    @Autowired
    SurgicalApplyRepository surgicalApplyRepository;

    @Transactional(rollbackFor = Exception.class)
    public void statusUpdate(Long accountId, Long orderId, Integer step){
        PackageOrderMappingStatus packageOrderMappingStatus = PackageOrderMappingStatus.convertByValue(step);
        if (packageOrderMappingStatus == null) {
            throw new AppException("訂單狀態更新失敗，狀態設定錯誤。");
        }
        
        Optional<PackageOrder> packageOrderOptional = packageOrderRepository.findById(orderId);
        if (!packageOrderOptional.isPresent()) {
            throw new AppException("訂單狀態更新失敗，查無此訂單! 訂單編號 : " + orderId);
        }
        
        PackageOrder packageOrder = packageOrderOptional.get();
        List<PackageOrderMapping> packageOrderMappings = packageOrder.getPackageOrderMappings();
        if (packageOrderMappings.isEmpty()) {
            throw new AppException("訂單狀態更新失敗，查無對應狀態紀錄資料!");
        }
        
        Boolean isPackageOrderMappingExist = packageOrderMappings.stream().anyMatch(pom -> pom.getStatus() == packageOrderMappingStatus.getValue());
        if (!isPackageOrderMappingExist){
            throw new AppException("訂單狀態更新失敗，查無對應狀態紀錄資料!");
        }
        
        SurgicalApply surgicalApply = packageOrder.getSurgicalApply();
        if (surgicalApply == null) {
            throw new AppException("訂單狀態更新失敗，查無對應手術單申請資料!");
        }
        
        List<PackageOrder> packageOrders = surgicalApply.getOrders();
        switch(packageOrderMappingStatus){
            case IN_USE:
                packageOrder.setStatus(PackageOrderStatus.IN_USE.getValue());
                packageOrderRepository.save(packageOrder);
                logger.info("SurgicalApply Status() = {}", surgicalApply.getStatus());
                if (surgicalApply.getStatus() == SurgicalApplyStatus.RECEIVED.getValue()){
                    surgicalApply.setStatus(SurgicalApplyStatus.USING.getValue());
                    surgicalApply.setUpdateTime(new Date());
                    surgicalApply.setUpdater(accountId);
                    surgicalApplyRepository.save(surgicalApply);
                }
                break;
            case FINISHED:
                packageOrder.setStatus(PackageOrderStatus.FINISH.getValue());
                packageOrderRepository.save(packageOrder);
                Boolean isOrdersFinished = packageOrders.stream().allMatch(po -> po.getStatus() == PackageOrderStatus.FINISH.getValue());
                if (isOrdersFinished){
                    surgicalApply.setStatus(SurgicalApplyStatus.DONE.getValue());
                    surgicalApply.setUpdateTime(new Date());
                    surgicalApply.setUpdater(accountId);
                    surgicalApplyRepository.save(surgicalApply);
                }else {
                    logger.info("尚有其它訂單資料未使用完成");
                }
                break;
            case CIRCULATE_CHECK:
                packageOrder.setStatus(PackageOrderStatus.CIRCULATE_CHECK_DONE.getValue());
                packageOrderRepository.save(packageOrder);
                Boolean isOrdersCirculateCheckDone = packageOrders.stream().allMatch(po -> po.getStatus() == PackageOrderStatus.CIRCULATE_CHECK_DONE.getValue());
                if (isOrdersCirculateCheckDone){
                    surgicalApply.setStatus(SurgicalApplyStatus.CIRCULATE_DONE.getValue());
                    surgicalApply.setUpdateTime(new Date());
                    surgicalApply.setUpdater(accountId);
                    surgicalApplyRepository.save(surgicalApply);
                }else {
                    logger.info("尚有其它訂單資料未回收清點完成");
                }
                break;
            default:
        }
    }
}
