package com.surgical.vo;

import lombok.Data;

@Data
public class ResponseMsg{

    private String msg = "";

    public ResponseMsg(String msg){
        this.msg = msg;
    }
}
