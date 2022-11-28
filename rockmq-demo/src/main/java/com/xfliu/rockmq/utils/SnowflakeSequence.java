package com.xfliu.rockmq.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * @description： 雪花算法生成器
 * @author： wlyg
 **/
public class SnowflakeSequence {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(getSequenceNo("9"));
        }
    }

    public static String getSequenceNo(String prefix) {
        Snowflake sf = new Snowflake(getWorkId(), getDataId(), true);
        if (StrUtil.isBlank(prefix)) {
            prefix = "";
        }
        return StrUtil.builder(prefix, sf.nextIdStr()).toString();
    }

    public static String getSequenceNo(String prefix, String postfix) {
        Snowflake sf = new Snowflake(getWorkId(), getDataId(), true);
        if (StrUtil.isBlank(prefix)) {
            prefix = "";
        }
        if (StrUtil.isBlank(postfix)) {
            postfix = "";
        }
        return StrUtil.builder(prefix, sf.nextIdStr(), postfix).toString();
    }

    /**
     * 根据 host address 取余，发生异常就获取 0到31之间的随机数
     */
    public static long getWorkId() {
        try {
            return getHostId(Inet4Address.getLocalHost().getHostAddress(), 31);
        } catch (UnknownHostException e) {
            return new Random().nextInt(32);
        }
    }

    /**
     * 根据 host name 取余，发生异常就获取 0到31之间的随机数
     */
    public static long getDataId() {
        try {
            return getHostId(Inet4Address.getLocalHost().getHostName(), 31);
        } catch (UnknownHostException e) {
            return new Random().nextInt(32);
        }
    }

    /**
     * 获取字符串s的字节数组，然后将数组的元素相加，对（max+1）取余
     */
    private static int getHostId(String s, int max) {
        byte[] bytes = s.getBytes();
        int sums = 0;
        for (int b : bytes) {
            sums += b;
        }
        return sums % (max + 1);
    }
}
