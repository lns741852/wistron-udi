package com.surgical.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class DeliverBatchDeliveryRequest{

    @NotBlank(message = "batchTitle cannot be empty or null")
    @Size(max = 50,message = "batchTitle cannot be longer than 50")
    private String batchTitle;

    @NotNull(message = "from cannot be null")
    private Long from;

    @NotNull(message = "to cannot be null")
    private Long to;

    @NotEmpty(message = "packages cannot be null")
    private List<Long> packages;
}
