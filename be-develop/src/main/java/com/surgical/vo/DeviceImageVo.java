package com.surgical.vo;

import com.surgical.entities.DeviceImage;

import lombok.Data;

@Data
public class DeviceImageVo{

    private String path;

    private Long id;

    private Boolean isMain;
    
    public DeviceImageVo() {
        
    }
    
    public DeviceImageVo(DeviceImage deviceImage) {
        this.path = deviceImage.getFileResource().getLocalPath();
        this.id = deviceImage.getFileResource().getId();
        this.isMain = deviceImage.getIsMain();
    }
}
