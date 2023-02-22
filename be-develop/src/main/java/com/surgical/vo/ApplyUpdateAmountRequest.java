package com.surgical.vo;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ApplyUpdateAmountRequest{
    
    @NotNull(message = "id cannot be null")
    private Long id; // add package amount record id
    
    private List<AddAmountPackageRequest> packages;
    
    private List<Long> removePackages;
}
