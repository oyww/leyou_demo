package com.leyou.other;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;

public class TestStringUtils {
    @Test
    public void testSubString()throws Exception{
        String strs="1,2,3,4,5,6,7,8,9";
        strs=null;
//        strs="";

        String substringBefore = StringUtils.substringBefore(strs, ",");
        System.out.println("substringBefore = " + substringBefore);
    }
}
