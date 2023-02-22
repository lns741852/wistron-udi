package com.surgical.vo;

import java.util.List;

public class DeviceTypeAddRequest{

    private Long id;

    private String name;

    private String spec;

    private String desc;

    private String nameScientific;

    private List<DeviceTypeImagesVo> images;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getSpec(){
        return spec;
    }

    public void setSpec(String spec){
        this.spec = spec;
    }

    public String getDesc(){
        return desc;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public String getNameScientific(){
        return nameScientific;
    }

    public void setNameScientific(String nameScientific){
        this.nameScientific = nameScientific;
    }

    public List<DeviceTypeImagesVo> getImages(){
        return images;
    }

    public void setImages(List<DeviceTypeImagesVo> images){
        this.images = images;
    }
}
