package com.surgical.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

@Data
public class PackagePackingReportRequest{

    
    @NotNull(message = "start cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date start;

    @NotNull(message = "end cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date end;

    @NotNull(message = "page cannot be null")
    private Integer page;
}
