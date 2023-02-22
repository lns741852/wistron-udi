package com.surgical.vo;

import lombok.Data;

@Data
public class DivisionPackagesVo{

    Long divisionId;

    Long configQty;

    Long packageQty;

    public DivisionPackagesVo(Long divisionId, Long configQty, Long packageQty){
        super();
        this.divisionId = divisionId;
        this.configQty = configQty;
        this.packageQty = packageQty;
    }
}
