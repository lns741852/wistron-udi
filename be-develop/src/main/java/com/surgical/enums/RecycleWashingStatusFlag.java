package com.surgical.enums;

import java.util.ArrayList;
import java.util.List;

public enum RecycleWashingStatusFlag{

    RECYCLE(8, 9),
    WASHING(10, 11);

    private Integer i;

    private Integer j;

    RecycleWashingStatusFlag(Integer i, Integer j){
        this.i = i;
        this.j = j;
    }

    public List<Integer> getStatusList(){
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(i);
        statusList.add(j);
        return statusList;
    }
}
