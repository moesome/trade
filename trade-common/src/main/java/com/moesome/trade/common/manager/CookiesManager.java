package com.moesome.trade.common.manager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


public class CookiesManager {
    public static void setCookie(String sessionId,int maxAge,HttpServletResponse httpServletResponse){
        if (httpServletResponse == null)
            return;
        Cookie cookie = new Cookie("sessionId",sessionId);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }
}
