package com.surgical.vo;

import lombok.Data;

@Data
public class DeviceTypeList{
    
    private Long id;
    private String name;
    private String spec;
    private String description;
    private String nameScientific;
    private Long totalQty;
    private String mainImagePath;
    
    public DeviceTypeList() {
        
    }
    public DeviceTypeList(Long id, String name,String spec, String description, String nameScientific,Long totalQty,String mainImagePath) {
    this.id = id;
    this.name = name;
    this.spec = spec;
    this.description = description;
    this.nameScientific = nameScientific;
    this.totalQty = totalQty;
    this.mainImagePath = mainImagePath;
}

}
