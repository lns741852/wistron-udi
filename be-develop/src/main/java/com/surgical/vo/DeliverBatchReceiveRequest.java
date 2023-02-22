package com.surgical.vo;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class DeliverBatchReceiveRequest{

    @NotNull(message = "deliverBatchId cannot be null")
    private Long deliverBatchId;

    @NotNull(message = "stationId cannot be null")
    private Long stationId;

    @NotNull(message = "packages cannot be null")
    private List<Long> packages;
}
