package com.netmania.checklod.data.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class DeviceIntentVo implements Serializable {
    private String mac;
    private String stickerNo;
    private String barcode;
    private String checkDate;
    private int reportTerm = 5;
}
