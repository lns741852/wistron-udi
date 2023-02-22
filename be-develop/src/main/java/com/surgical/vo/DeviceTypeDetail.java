package com.surgical.vo;

import java.util.List;
import java.util.Map;

public class DeviceTypeDetail{
    
    private Long id;
    
    private String name;
    
    private String spec;

    private String desc;
    
    private String nameScientific;
    
    private List<Map<String, Object>> images;
    
    private List<Map<String,Object>> division;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
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

    public void setDesc(String description){
        this.desc = description;
    }

    public String getNameScientific(){
        return nameScientific;
    }

    public void setNameScientific(String nameScientific){
        this.nameScientific = nameScientific;
    }
    
    public List<Map<String, Object>> getImages(){
        return images;
    }
    
    public void setImages(List<Map<String,Object>> images) {
        this.images = images;
    }
    
    public List<Map<String, Object>> getDivision(){
        return division;
    }
    
    public void setDivision(List<Map<String,Object>> division) {
        this.division = division;
    }
    
}
