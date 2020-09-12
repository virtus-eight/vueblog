package com.vera.shiro;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.vera.common.lang.Result;
import com.vera.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends AuthenticatingFilter {
    @Autowired
    private JwtUtils jwtUtils;

    //生成Token   后会被继承的executeLogin进行调用
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        //获取Jwt 从header里面获取
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        /*Authorization http请求中包含的*/
        String jwt = request.getHeader("Authorization");
        //判断jwt是否为空
        if(StringUtils.isEmpty(jwt)){
            //为空就跳过
            return  null;
        }
        //返回一个自定义的Token
        return new JwtToken(jwt);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        //获取Jwt 从header里面获取
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        /*Authorization http请求中包含的*/
        String jwt = request.getHeader("Authorization");
        //判断jwt是否为空
        if(StringUtils.isEmpty(jwt)){
            //为空就直接进行权限过滤 不进行拦截 给注解拦截
            return  true;
        }else {
             //校验jwt
            Claims claimByToken = jwtUtils.getClaimByToken(jwt);
            if (claimByToken==null||jwtUtils.isTokenExpired(claimByToken.getExpiration())){
                /*如果校验失败或者Token过期 抛出异常*/
                throw  new ExpiredCredentialsException("token已失效，请重新登录");
            }
            //执行登录
            return executeLogin(servletRequest,servletResponse);
        }
    }

    //登录失败会跳转到这个方法
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpServletResponse= (HttpServletResponse) response;
        Throwable throwable = e.getCause() == null?e:e.getCause();
        //把错误信息传递给Result
        Result result = Result.fail(throwable.getMessage());
        /*把这个result转换成json格式*/
        String json = JSONUtil.toJsonStr(result);
        try {
            httpServletResponse.getWriter().print(json);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return false;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
