package com.leyou.service;

import commons.utils.TimeUtils;
import org.junit.Test;

public class GetTime {
    TimeUtils timeUtils=TimeUtils.getUtils();
    @Test
    public void testTimeUtils()throws Exception{
        timeUtils.computeTime();
        Thread.sleep(2673);
        timeUtils.computeTime();
        timeUtils=null;
    }
}
