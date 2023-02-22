package com.surgical.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PackageOrderCreateRequest {
	
	@NotNull(message = "surgicalApplyId cannot be null")
    private Long surgicalApplyId;
	
	@Valid
    private List<SurgicalApplyCreateOrdersRequest> orders;
}
