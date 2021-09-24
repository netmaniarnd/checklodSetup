package com.netmania.checklod.data.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class TemperatureRangeItemVo implements Serializable {
    public int idx;
    public int minimum;
    public int maximum;
}
