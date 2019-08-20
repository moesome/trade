package com.moesome.trade.zuul.filter.pre;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moesome.trade.common.response.Result;
import com.moesome.trade.user.common.response.vo.UserDetailVo;

import com.moesome.trade.zuul.manager.SessionManager;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@Slf4j
public class AuthFilter extends ZuulFilter{
    @Autowired
    private SessionManager sessionManager;

    private final String AUTHORIZED_ERR;

    public AuthFilter() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        AUTHORIZED_ERR = objectMapper.writeValueAsString(Result.AUTHORIZED_ERR);
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 2;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        HashSet<String> matches = new HashSet<>(2);
        // 注册
        matches.add("POST-/users");
        // 登录
        matches.add("POST-/users/login");
        // 商品展示
        matches.add("GET-/commodities");
        matches.add("GET-/recharge/return");
        matches.add("GET-/recharge/notify");
        log.debug("接收到路由"+method+"-"+requestURI);
        if (matches.contains(method+"-"+requestURI)){
            log.debug(request.getLocalAddr()+"->"+method+"->"+requestURI+"跳过授权");
            return false;
        }
        log.debug(request.getLocalAddr()+"->"+method+"->"+requestURI+"授权拦截器拦截");
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals("sessionId")){
                    sessionId = cookie.getValue();
                }
            }
            log.debug("cookies 中成功获取 session ，"+request.getLocalAddr()+"->"+request.getRequestURI()+"->sessionId="+sessionId);
            // 校验
            if (!StringUtils.isEmpty(sessionId)){
                // 找到用户
                String userId = sessionManager.getSessionMessage(sessionId,"userId");
                if (userId != null){
                    log.debug("认证成功"+request.getLocalAddr()+"->"+request.getRequestURI()+"->sessionId="+sessionId+"添加用户 ID 头："+userId);
                    requestContext.addZuulRequestHeader("user-id",userId);
                    return null;
                }
            }
        }
        // 到这里说明认证失败
        log.debug(request.getLocalAddr()+"->"+request.getRequestURI()+"->sessionId="+sessionId+"认证失败");
        requestContext.setSendZuulResponse(false);
        requestContext.getResponse().setCharacterEncoding("utf-8");
        requestContext.setResponseBody(AUTHORIZED_ERR);
        requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        return null;
    }
}
