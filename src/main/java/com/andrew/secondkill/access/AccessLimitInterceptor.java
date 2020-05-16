package com.andrew.secondkill.access;

import com.alibaba.fastjson.JSON;
import com.andrew.secondkill.domain.User;
import com.andrew.secondkill.redis.AccessKeyPrefix;
import com.andrew.secondkill.redis.RedisService;
import com.andrew.secondkill.result.CodeMsg;
import com.andrew.secondkill.result.Result;
import com.andrew.secondkill.service.UserService;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * AccessLimitInterceptor Class
 *
 * @author andrew
 * @date 2020/4/4
 */
@Service
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(handler instanceof HandlerMethod){

            //登录信息注入 不管有没有添加注解都需要插入登录信息
            User user = getUser(request,response);
            UserContext.setLocalUser(user);

            HandlerMethod mh = (HandlerMethod)handler;
            AccessLimit accessLimit = mh.getMethodAnnotation(AccessLimit.class);

            //没有添加注解的直接通过
            if(accessLimit==null){
                return true;
            }

            //注解中的内容
            boolean needLogin = accessLimit.needLogin();
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();

            String key = request.getRequestURI();

            //是否需要登录
            if(needLogin){
                if(user==null){
                    //没有登录返回给前端
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key = key+"_"+user.getId();
            }else{
                //不需要登录
                return true;
            }

            //限流 x秒内允许访问x次---->根据注解的参数设置
            AccessKeyPrefix accessKeyPrefix = AccessKeyPrefix.withExpire(seconds);

            //存放在redis中-->URL路径_userId  记录次数
            Integer count = redisService.get(accessKeyPrefix, key, Integer.class);
            if(count==null){
                redisService.set(accessKeyPrefix,key,1);
            }else if(count < maxCount ){
                redisService.incr(accessKeyPrefix,key);
            }else{
                render(response,CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }

        return true;
    }

    private void render(HttpServletResponse response, CodeMsg sessionError) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String result = JSON.toJSONString(Result.error(sessionError));
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(result.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response){
        /**
         * 有时其他客户端不会把token放在cookie中，会放在request参数里
         */
        String paramToken = request.getParameter(UserService.COOKIE_TOKEN_NAME);
        String cookieToke = getCookieValue(request,UserService.COOKIE_TOKEN_NAME);
        if(StringUtils.isEmpty(cookieToke)&&StringUtils.isEmpty(paramToken)){
            return null;
        }
        String notNullToken = StringUtils.isEmpty(cookieToke) ? paramToken : cookieToke;

        User user = userService.getByToken(request,response,notNullToken);
        return user;
    }

    private String getCookieValue(HttpServletRequest request,String tokenName){
        Cookie[] cookies = request.getCookies();
        if(cookies==null||cookies.length<=0){
            return null;
        }
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(tokenName)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
