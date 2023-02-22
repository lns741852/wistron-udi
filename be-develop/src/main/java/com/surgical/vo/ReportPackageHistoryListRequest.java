package com.surgical.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class ReportPackageHistoryListRequest{

    private String configCode;

    private String serialNo;

    @NotBlank(message = "start cannot be blank")
    @Pattern(regexp = "^\\d{4}[\\-](0[1-9]|1[012])[\\-](0[1-9]|[12][0-9]|3[01])$", message = "pattern : yyyy-MM-dd")
    private String start;

    @NotBlank(message = "end cannot be blank")
    @Pattern(regexp = "^\\d{4}[\\-](0[1-9]|1[012])[\\-](0[1-9]|[12][0-9]|3[01])$", message = "pattern : yyyy-MM-dd")
    private String end;

    @Pattern(regexp = "^\\d{4}[\\-](0[1-9]|1[012])[\\-](0[1-9]|[12][0-9]|3[01])$", message = "pattern : yyyy-MM-dd")
    private String sterilizationDate;

    private String sterilizer;

    private String medicalRecordNo;

    private Integer page;
}
