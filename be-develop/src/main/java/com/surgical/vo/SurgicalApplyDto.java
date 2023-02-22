package com.surgical.vo;

import lombok.Data;

@Data
public class SurgicalApplyDto{

    private Long applyId;

    private Boolean count = false;
    
    private Long trackingId;
}
