package com.surgical.vo;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SterilizedBatchCreateRequest{

    @NotBlank(message = "batchName cannot be null or empty")
    private String batchName;
    
    @NotBlank(message = "sterilizer cannot be null or empty")
    private String sterilizer;

    @NotBlank(message = "petriDish cannot be null or empty")
    @Size(max = 30, message = "petriDish length cannot be longer than 30")
    private String petriDish;
    
    @NotNull(message = "startTime cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @NotNull(message = "packages cannot be null")
    private List<Long> packages;
}
