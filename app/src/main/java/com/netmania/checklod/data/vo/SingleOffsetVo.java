package com.netmania.checklod.data.vo;

import lombok.Data;

@Data
public class SingleOffsetVo extends BaseVo {
    private int idx;

    // 실제값 / 100;
    private int index;
    private int value1;//-80
}
