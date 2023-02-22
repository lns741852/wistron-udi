package com.surgical.vo;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ApplyAddAmountRequest{


    @NotNull(message = "configId cannot be null")
    private Long configId;
    
    @NotNull(message = "package cannot be null")
    private List<AddAmountPackageRequest> packages;
    
}
