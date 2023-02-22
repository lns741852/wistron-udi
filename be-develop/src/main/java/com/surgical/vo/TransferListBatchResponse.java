package com.surgical.vo;

import java.util.List;

import lombok.Data;

@Data
public class TransferListBatchResponse{

    private Long batchId;

    private String batchTitle;

    private Integer batchStatus;

    private List<DeliverBatchPackageListInfoVo> packages;
}
