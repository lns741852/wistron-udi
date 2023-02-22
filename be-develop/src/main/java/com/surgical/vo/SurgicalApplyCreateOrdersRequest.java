package com.surgical.vo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SurgicalApplyCreateOrdersRequest{

    @NotNull(message = "configId cannot be null")
    private Long configId;

    @NotNull(message = "qty cannot be null")
    @Min(value = 1, message = "qty must greater than 1")
    private Integer qty;
}
