package com.surgical.vo;

import java.util.Set;

import lombok.Data;

@Data
public class FileDeleteVo{
    
    private Set<Long> fileIds;
    
    private Boolean checkMapping;
}
