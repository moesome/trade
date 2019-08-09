package com.moesome.trade.zuul.filter.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moesome.trade.common.response.Result;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

@Slf4j
//@Component
public class ServiceExceptionFilter extends ZuulFilter {
    private final String CLIENT_ERROR;
    private final String SERVER_ERROR;

    public ServiceExceptionFilter() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CLIENT_ERROR = objectMapper.writeValueAsString(Result.CLIENT_ERROR);
        SERVER_ERROR = objectMapper.writeValueAsString(Result.SERVER_ERROR);
    }

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        HttpServletRequest request = requestContext.getRequest();
        int status = response.getStatus();
        if (status >= 400 && status <= 599){
            log(request,status);
            return true;
        }else{
            return false;
        }
    }

    private void log(HttpServletRequest request,int status){
        StringBuilder logMessage = new StringBuilder();
        Enumeration<String> parameterNames = request.getParameterNames();
        logMessage.append("response status: ").append(status).append("\n");
        logMessage.append("error occur!").append("\n");
        logMessage.append("request ").append(request.getMethod()).append(" ").append(request.getRequestURI()).append("\n");
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sessionId")) {
                    sessionId = cookie.getValue();
                }
            }
        }
        logMessage.append("sessionId:").append(sessionId).append("\n");
        logMessage.append("request param: {");
        while (parameterNames.hasMoreElements()){
            String elementName = parameterNames.nextElement();
            logMessage.append(elementName).append(":").append(request.getParameter(elementName)).append(",");
        }
        logMessage.replace(logMessage.length() -1,logMessage.length(),"").append("}").append("\n");
        BufferedReader br;
        try {
            br = request.getReader();
            String str;
            StringBuilder wholeStr = new StringBuilder();
            while((str = br.readLine()) != null) {
                wholeStr.append(str);
            }
            logMessage.append("body:").append(wholeStr).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.error(logMessage.toString());
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        int status = response.getStatus();
        if (status < 500){
            requestContext.setResponseBody(CLIENT_ERROR);
        }else{
            requestContext.setResponseBody(SERVER_ERROR);
        }
        requestContext.setSendZuulResponse(false);
        requestContext.getResponse().setCharacterEncoding("utf-8");
        return null;
    }
}
