package com.netmania.checklod.data.vo;

import lombok.Data;

@Data
public class OffsetVo extends BaseVo {
    private int idx;

    // 실제값 / 100;
    private int value1;//-80
    private int value2;//-40
    private int value3;//-20
    private int value4;//5
    private int value5;//20
}
