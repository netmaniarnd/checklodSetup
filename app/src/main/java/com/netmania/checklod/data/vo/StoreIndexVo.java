package com.netmania.checklod.data.vo;

import lombok.Data;

@Data
public class StoreIndexVo extends BaseVo {
    private int idx;

    private int startIndex;
    private String startRtc;
    private int endIndex;
    private String endRtc;
    private int totalIndex;
}
