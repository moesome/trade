package com.moesome.trade.common.util;

import org.springframework.util.StringUtils;

public class Format {
    public static String orderFormat(String order){
        if (StringUtils.isEmpty(order) || order.equals("ascend")){
            order = "ASC";
        }else{
            order = "DESC";
        }
        return order;
    }
    public static String pageFormat(String page){
        int i;
        if (StringUtils.isEmpty(page)){
            i = 1;
        }else{
            i = Integer.parseInt(page);
            if (i <= 0){
                i = 1;
            }
        }
        return Integer.toString(i);
    }
}
