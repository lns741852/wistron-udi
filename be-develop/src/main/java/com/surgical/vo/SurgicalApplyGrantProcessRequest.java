package com.surgical.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Valid
public class SurgicalApplyGrantProcessRequest{

    @NotNull(message = "orderId cannot be null")
    private Long orderId;

    @Valid
    @NotEmpty(message = "orders cannot be null or empty")
    private List<SurgicalApplyGrantProcessRequestOrderPart> orders;
}
