package com.moesome.trade.zuul.filter.pre;

import com.moesome.trade.common.util.Format;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class FormatFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String page = request.getParameter("page");
        String order = request.getParameter("order");
        return !StringUtils.isEmpty(page) || !StringUtils.isEmpty(order);
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String page = request.getParameter("page");
        String order = request.getParameter("order");
        page = Format.pageFormat(page);
        order = Format.orderFormat(order);
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("page", Collections.singletonList(page));
        map.put("order", Collections.singletonList(order));
        log.info(map.toString());
        requestContext.setRequestQueryParams(map);
        return null;
    }
}
