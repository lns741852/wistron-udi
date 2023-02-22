package com.surgical.vo;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SurgicalApplyCancelRequest{

    @NotNull(message = "The field 'orderId' cannot be null")
    private Long orderId;
}
