package com.surgical.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.surgical.entities.DeliverBatch;
import com.surgical.entities.DeliverBatchDetail;
import com.surgical.entities.Package;
import com.surgical.entities.SterilizedBatch;
import com.surgical.entities.SterilizedBatchDetail;
import com.surgical.enums.PackageStatus;
import com.surgical.exception.AppException;
import com.surgical.repositories.DeliverBatchRepository;
import com.surgical.repositories.PackageRepository;
import com.surgical.repositories.SterilizedBatchRepository;
import com.surgical.vo.DeliverBatchPackageListInfoVo;
import com.surgical.vo.TransferListBatchResponse;

@Service
public class BatchTransferService{

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private SterilizedBatchRepository sterilizedBatchRepository;
    
    @Autowired
    private DeliverBatchRepository deliverBatchRepository;


    public Page<TransferListBatchResponse> transferList(Integer type, Pageable pageable){
        if (type.equals(0)){
            List<Package> packages = packageRepository.findByStatusIn(Arrays.asList(PackageStatus.STER_RECV.getValue(), PackageStatus.STER_FAIL.getValue()));
            List<Long> trackingIds = packages.stream().map(Package::getTrackingId).collect(Collectors.toList());
            if(trackingIds.isEmpty()) {
                trackingIds.add(0L);
            }
            Page<DeliverBatch> deliverBatchPage = deliverBatchRepository.getUnsterilizedDeliverBatch(trackingIds, pageable);
            Page<TransferListBatchResponse> responsePage = deliverBatchPage.map(new Function<DeliverBatch, TransferListBatchResponse>(){

                @Override
                public TransferListBatchResponse apply(DeliverBatch deliverBatch){
                    TransferListBatchResponse transferListBatchResponse = new TransferListBatchResponse();
                    transferListBatchResponse.setBatchId(deliverBatch.getId());
                    transferListBatchResponse.setBatchStatus(deliverBatch.getStatus());
                    transferListBatchResponse.setBatchTitle(deliverBatch.getTitle());
                    List<DeliverBatchPackageListInfoVo> packageList = new ArrayList<DeliverBatchPackageListInfoVo>();
                    for(DeliverBatchDetail detail : deliverBatch.getDeliverBatchDetails()){
                        if (trackingIds.contains(detail.getTrackingId())){
                            Package packageEntity = packages.stream().filter(p -> p.getTrackingId().equals(detail.getTrackingId())).findAny().get();
                            packageList.add(setPackageListInfoVo(packageEntity));
                        }else{
                            continue;
                        }
                    }
                    transferListBatchResponse.setPackages(packageList);
                    return transferListBatchResponse;
                }
            });
            return responsePage;
        }else if (type.equals(1)){
            List<Package> packages = packageRepository.findByStatus(PackageStatus.STER_DONE.getValue());
            List<Long> trackingIds = packages.stream().map(Package::getTrackingId).collect(Collectors.toList());
            if(trackingIds.isEmpty()) {
                trackingIds.add(0L);
            }
            Page<SterilizedBatch> sterilizedBatchPage = sterilizedBatchRepository.getUnsentSterilizedBatch(trackingIds, pageable);
            Page<TransferListBatchResponse> responsePage = sterilizedBatchPage.map(new Function<SterilizedBatch, TransferListBatchResponse>(){

                @Override
                public TransferListBatchResponse apply(SterilizedBatch sterilizedBatch){
                    TransferListBatchResponse transferListBatchResponse = new TransferListBatchResponse();
                    transferListBatchResponse.setBatchId(sterilizedBatch.getId());
                    transferListBatchResponse.setBatchStatus(sterilizedBatch.getStatus());
                    transferListBatchResponse.setBatchTitle(sterilizedBatch.getName());
                    List<DeliverBatchPackageListInfoVo> packageList = new ArrayList<DeliverBatchPackageListInfoVo>();
                    for(SterilizedBatchDetail detail : sterilizedBatch.getSterilizedBatchDetails()){
                        if (trackingIds.contains(detail.getTrackingId())){
                            Package packageEntity = packages.stream().filter(p -> p.getTrackingId().equals(detail.getTrackingId())).findAny().get();
                            packageList.add(setPackageListInfoVo(packageEntity));
                        }else{
                            continue;
                        }
                    }
                    transferListBatchResponse.setPackages(packageList);
                    return transferListBatchResponse;
                }
            });
            return responsePage;
        }else{
            throw new AppException("非預期流程");
        }
    }

    private DeliverBatchPackageListInfoVo setPackageListInfoVo(Package packageEntity){
        DeliverBatchPackageListInfoVo vo = new DeliverBatchPackageListInfoVo();
        vo.setId(packageEntity.getId());
        vo.setQrcode(packageEntity.getDeviceBoxQrcode());
        vo.setTrackingId(packageEntity.getTrackingId());
        vo.setConfigId(packageEntity.getPackageConfigId());
        vo.setConfigCode(packageEntity.getPackageConfig().getConfigCode());
        vo.setConfigName(packageEntity.getPackageConfig().getPackageName());
        vo.setDivisionId(packageEntity.getPackageConfig().getDivisionId());
        vo.setStatus(packageEntity.getStatus());
        return vo;
    }
}
