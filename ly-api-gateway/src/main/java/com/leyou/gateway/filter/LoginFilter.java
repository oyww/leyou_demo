package com.leyou.gateway.filter;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.properties.FilterProperties;
import com.leyou.gateway.properties.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProp;

    @Autowired
    private FilterProperties filterProp;

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public String filterType() {
        //return FilterConstants.PRE_TYPE;//pre
        return "pre";
    }

    @Override
    //处理请求头之前过滤（5-1），过滤处理请求参数
    public int filterOrder() {
        //int order = FilterConstants.PRE_DECORATION_FILTER_ORDER;//常量5（1-10）
        return 5;//优先级，不要设置太低，留下前面的位置备用，越低越高
    }

    @Override
    public boolean shouldFilter() {//返回true要拦截处理，返回false不拦截
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest req = ctx.getRequest();
        // 获取路径
        String requestURI = req.getRequestURI();// /xxx/yyy 这个使用startWith
//        String url = req.getRequestURL().toString();// http://xxx/yyy... 这个使用contains
//        List<String> allowPaths = this.filterProp.getAllowPaths();
        //也可以根据方法请求方式判断放行，比如get
        String method = req.getMethod();
        logger.info("[授权中心] 当前请求的方式是：{}",method);
        // 判断白名单
        for (String path : this.filterProp.getAllowPaths()) {
            // 然后判断是否是符合
            if (requestURI.startsWith(path)) {
//                return true;
                return false;
            }
        }
        return true;
    }


    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();
        // 获取cookie中的token
        String token = CookieUtils.getCookieValue(request, jwtProp.getCookieName());
        // 校验
        try {
            // 校验通过什么都不做，即放行
            JwtUtils.getInfoFromToken(token, jwtProp.getPublicKey());
            //TODO 可以获取载荷信息做权限校验判断
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProp.getPublicKey());
        } catch (Exception e) {//拦截处理：可以通过if判断或异常捕获进行
            // 校验出现异常，返回403
            ctx.setSendZuulResponse(false);//不转发，默认true就是放行
            ctx.setResponseStatusCode(886);//自定义状态码
//            ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());//403
            logger.error("非法访问，未登录，地址：{}", request.getRemoteHost(), e);
        }
        return null;
    }
}