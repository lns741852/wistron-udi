package com.surgical.vo;

import java.sql.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class WashingBatchCreateRequest{

    @NotBlank(message = "batchName cannot be null")
    @Size(max = 50, message = "batchName length cannot be longer than 50")
    private String batchName;

    @NotBlank(message = "washing machine cannot be null")
    @Size(max = 15, message = "washingMachine length cannot be longer than 15")
    private String washingMachine;

    @NotNull(message = "startTime cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @NotEmpty(message = "packages cannot be null or empty")
    private List<Long> packages;
}
