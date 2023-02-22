package com.surgical.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Valid
public class SurgicalApplyGrantProcessRequestOrderPart{

    @NotNull(message = "configId cannot be null")
    private Long configId;

    @NotEmpty(message = "packages cannot be null or empty")
    private List<Long> packages;
}
