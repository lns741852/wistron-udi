package com.surgical.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class SystemPropertiesRequestVo{

    @NotBlank(message = "名稱或內容不可為空")
    @Size(max = 50, message = "資料長度超過上限(50字)") 
    private String name;

    @NotBlank(message = "名稱或內容不可為空")
    @Size(max = 500, message = "資料長度超過上限(500字)") 
    private String value;
    
    @Size(max = 50, message = "資料長度超過上限(50字)") 
    private String description;
}
