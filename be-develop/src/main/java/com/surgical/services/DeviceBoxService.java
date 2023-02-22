package com.surgical.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surgical.entities.DeviceBox;
import com.surgical.entities.DeviceBoxRepair;
import com.surgical.enums.ComponentStatusFlag;
import com.surgical.exception.AppException;
import com.surgical.repositories.DeviceBoxRepairRepository;
import com.surgical.repositories.DeviceBoxRepository;
import com.surgical.vo.CreateDeviceBox;
import com.surgical.vo.DeviceBoxDetailVo;
import com.surgical.vo.DeviceBoxMaintainInfo;
import com.surgical.vo.DeviceBoxRepairVo;
import com.surgical.vo.DeviceBoxListVo;

@Service
public class DeviceBoxService{

    @Autowired
    DeviceBoxRepository deviceBoxRepository;

    @Autowired
    DeviceBoxRepairRepository deviceBoxRepairRepository;

    public void add(Long accountId, CreateDeviceBox deviceBoxRequest){
        Optional<DeviceBox> oriDeviceBoxOptional;
        if ("".equals(deviceBoxRequest.getUdi())){
            oriDeviceBoxOptional = deviceBoxRepository.findByQrcode(deviceBoxRequest.getQrcode());
        }else{
            oriDeviceBoxOptional = deviceBoxRepository.findFirstByQrcodeOrUdi(deviceBoxRequest.getQrcode(), deviceBoxRequest.getUdi());
        }
        if (oriDeviceBoxOptional.isPresent()){
            DeviceBox oriDeviceBox = oriDeviceBoxOptional.get();
            if (oriDeviceBox.getQrcode().equals(deviceBoxRequest.getQrcode())){
                if (oriDeviceBox.getUdi() != null && !"".equals(oriDeviceBox.getUdi()) && oriDeviceBox.getUdi().equals(deviceBoxRequest.getUdi())){
                    throw new AppException("Qrcode 及 Udi 皆重複");
                }
                throw new AppException("Qrcode 重複");
            }else{
                throw new AppException("Udi 重複");
            }
        }
        Date now = new Date();
        DeviceBox deviceBox = new DeviceBox();
        deviceBox.setQrcode(deviceBoxRequest.getQrcode());
        deviceBox.setCost(deviceBoxRequest.getCost());
        deviceBox.setStatus(0);
        deviceBox.setCreateTime(now);
        deviceBox.setUdi(deviceBoxRequest.getUdi());
        deviceBox.setUpdater(accountId);
        deviceBox.setUpdateTime(now);
        deviceBox.setUsedCount(0L);
        deviceBoxRepository.save(deviceBox);
    }

    public DeviceBoxDetailVo details(String qrcode){
        Optional<DeviceBox> deviceBoxOpt = deviceBoxRepository.findByQrcode(qrcode);
        DeviceBox deviceBox = deviceBoxOpt.orElseThrow(() -> new AppException("此 qrcode 不存在"));
        DeviceBoxDetailVo deviceBoxDetailVo = new DeviceBoxDetailVo();
        deviceBoxDetailVo.setId(deviceBox.getId());
        deviceBoxDetailVo.setQrcode(deviceBox.getQrcode());
        deviceBoxDetailVo.setUdi(deviceBox.getUdi());
        deviceBoxDetailVo.setCost(deviceBox.getCost());
        deviceBoxDetailVo.setStatus(deviceBox.getStatus());
        deviceBoxDetailVo.setUsedCount(deviceBox.getUsedCount());
        deviceBoxDetailVo.setCreateTime(deviceBox.getCreateTime());
        deviceBoxDetailVo.setUsedTime(deviceBox.getUsedTime());
        List<DeviceBoxRepair> deviceBoxRepairs = deviceBoxRepairRepository.findByDeviceBoxIdOrderByCreateTimeAsc(deviceBox.getId());
        List<DeviceBoxRepairVo> deviceBoxRepairVos = new ArrayList<DeviceBoxRepairVo>();
        for(DeviceBoxRepair deviceBoxRepair : deviceBoxRepairs){
            DeviceBoxRepairVo deviceBoxRepairVo = new DeviceBoxRepairVo();
            deviceBoxRepairVo.setId(deviceBoxRepair.getId());
            deviceBoxRepairVo.setPreUsedCount(deviceBoxRepair.getPreviousUsedCount());
            deviceBoxRepairVo.setStatus(deviceBoxRepair.getStatus());
            deviceBoxRepairVo.setComments(deviceBoxRepair.getComments());
            deviceBoxRepairVo.setCreateTime(deviceBoxRepair.getCreateTime());
            deviceBoxRepairVo.setFinishTime(( deviceBoxRepair.getStatus() == ComponentStatusFlag.SCRAPPED.getValue() ) ? deviceBox.getScrapTime() : deviceBoxRepair.getFinishTime());
            deviceBoxRepairVos.add(deviceBoxRepairVo);
        }
        deviceBoxDetailVo.setRecords(deviceBoxRepairVos);
        deviceBoxDetailVo.setRepairCount(deviceBoxRepairs.stream().filter(dbr -> dbr.getStatus() != ( ComponentStatusFlag.SCRAPPED.getValue() )).count());
        return deviceBoxDetailVo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(DeviceBoxMaintainInfo deviceBoxMaintain, Long accountId, ComponentStatusFlag statusFlag){
        Optional<DeviceBox> deviceBoxOptional = deviceBoxRepository.findByQrcode(deviceBoxMaintain.getQrcode());
        if (!deviceBoxOptional.isPresent()){
            throw new AppException("此 qrcode 不存在");
        }
        DeviceBox deviceBox = deviceBoxOptional.get();
        Integer status = statusFlag.getValue();
        Date now = new Date();
        switch(statusFlag){
            case REPAIR:
                if (deviceBox.getStatus() != ComponentStatusFlag.REPLACED.getValue() && deviceBox.getStatus() != ComponentStatusFlag.NORMAL.getValue()){
                    throw new AppException("非可執行維修狀態");
                }
                updateDeviceBox(deviceBox, status, accountId, now);
                createDeviceBoxRepair(new DeviceBoxRepair(), deviceBox, status, deviceBoxMaintain.getComments(), accountId, now);
                break;
            case REPAIR_DONE:
                if (deviceBox.getStatus() != ComponentStatusFlag.REPAIR.getValue()){
                    throw new AppException("非可執行維修完成狀態");
                }
                Optional<DeviceBoxRepair> deviceBoxRepairOptional = deviceBoxRepairRepository.findByDeviceBoxIdAndStatusAndFinishTimeIsNull(deviceBox.getId(), deviceBox.getStatus());
                updateDeviceBox(deviceBox, ComponentStatusFlag.REPLACED.getValue(), accountId, now);
                if (deviceBoxRepairOptional.isPresent()){
                    updateDeviceBoxRepair(status, now, deviceBoxRepairOptional.get(), accountId);
                }else{
                    throw new AppException("此維修紀錄不存在");
                }
                break;
            case SCRAPPED:
                if (deviceBox.getStatus() == ComponentStatusFlag.NORMAL.getValue() || deviceBox.getStatus() == ComponentStatusFlag.REPLACED.getValue() || deviceBox.getStatus() == ComponentStatusFlag.REPAIR.getValue()){
                    updateDeviceBox(deviceBox, status, accountId, now);
                    createDeviceBoxRepair(new DeviceBoxRepair(), deviceBox, status, deviceBoxMaintain.getComments(), accountId, now);
                }else{
                    throw new AppException("非可執行報廢狀態");
                }
                break;
            default:
                throw new AppException("非可更新狀態");
        }
    }

    private DeviceBox updateDeviceBox(DeviceBox deviceBox, Integer status, Long accountId, Date now){
        deviceBox.setStatus(status);
        deviceBox.setUpdater(accountId);
        deviceBox.setUpdateTime(now);
        if (status == ComponentStatusFlag.SCRAPPED.getValue()){
            deviceBox.setScrapTime(now);
        }
        deviceBoxRepository.save(deviceBox);
        return deviceBox;
    }

    private void createDeviceBoxRepair(DeviceBoxRepair deviceBoxRepair, DeviceBox deviceBox, Integer status, String comments, Long accountId, Date now){
        deviceBoxRepair.setDeviceBoxId(deviceBox.getId());
        deviceBoxRepair.setPreviousUsedCount(deviceBox.getUsedCount() - deviceBoxRepairRepository.getTotalPreviousUsedCount(deviceBox.getId()));
        deviceBoxRepair.setComments(comments);
        updateDeviceBoxRepair(status, now, deviceBoxRepair, accountId);
    }

    private void updateDeviceBoxRepair(Integer status, Date now, DeviceBoxRepair deviceBoxRepair, Long accountId){
        deviceBoxRepair.setStatus(status);
        deviceBoxRepair.setUpdater(accountId);
        if (status.equals(ComponentStatusFlag.REPAIR_DONE.getValue())){
            deviceBoxRepair.setFinishTime(now);
        }else{
            deviceBoxRepair.setCreateTime(now);
        }
        deviceBoxRepairRepository.save(deviceBoxRepair);
    }
    
    public Map<String, Object> deviceBoxList (Integer status, Pageable page){
        Map<String, Object>result = new HashMap<String, Object>();         
        result.put("brand" , "BR");
        result.put("manufacture_id" , "BR01");
        result.putAll(deviceBoxRepository.countNumbers());
        Specification<DeviceBox> specification = new Specification<DeviceBox>(){
            @Override
            public Predicate toPredicate(Root<DeviceBox> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (status == null){
                    predicates.add(criteriaBuilder.notEqual(root.get("status"), 9));
                }else{
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                Order asc = criteriaBuilder.asc(root.get("id"));
                query.orderBy(asc);
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
        Page<DeviceBox> deviceBoxesPage = deviceBoxRepository.findAll(specification, page);
        Page<DeviceBoxListVo> deviceBoxListPage = deviceBoxesPage.map(new Function<DeviceBox, DeviceBoxListVo>(){
            @Override
            public DeviceBoxListVo apply(DeviceBox deviceBox){
                DeviceBoxListVo deviceBoxListVo = new DeviceBoxListVo();
                deviceBoxListVo.setId(deviceBox.getId());
                deviceBoxListVo.setStatus(deviceBox.getStatus());
                deviceBoxListVo.setCreateTime(deviceBox.getCreateTime());
                deviceBoxListVo.setUsedTime(deviceBox.getUsedTime());
                deviceBoxListVo.setUdi(deviceBox.getUdi());
                deviceBoxListVo.setQrcode(deviceBox.getQrcode());
                deviceBoxListVo.setCost(deviceBox.getCost());
                deviceBoxListVo.setUsedCount(deviceBox.getUsedCount());
                return deviceBoxListVo;
            }
        });
        result.put("boxes",deviceBoxListPage);
        return result;
    }
    
    public Map<String, Object> getDeviceBoxInfo(String qrcode){
        Optional<DeviceBox> deviceBoxOptional = deviceBoxRepository.findByQrcode(qrcode);
        if (!deviceBoxOptional.isPresent()){
            throw new AppException("Qrcode對應DeviceBox不存在");
        }
        DeviceBox deviceBox = deviceBoxOptional.get();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("id", deviceBox.getId());
        resultMap.put("qrcode", deviceBox.getQrcode());
        resultMap.put("status", deviceBox.getStatus());
        return resultMap;
    }
}
