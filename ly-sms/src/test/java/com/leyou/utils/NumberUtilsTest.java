package com.leyou.utils;

import org.junit.Test;

public class NumberUtilsTest {
    @Test
    public void generateCode() throws Exception {
        System.out.println(NumberUtils.generateCode(6));
    }
    @Test
    public void testCode()throws Exception{
        System.out.println(NumberUtils.generateCode());
    }

}