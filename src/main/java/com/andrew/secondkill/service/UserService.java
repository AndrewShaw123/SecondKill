package com.andrew.secondkill.service;

import com.andrew.secondkill.dao.UserDao;
import com.andrew.secondkill.domain.User;
import com.andrew.secondkill.exception.GlobalException;
import com.andrew.secondkill.redis.RedisService;
import com.andrew.secondkill.redis.UserKeyPrefix;
import com.andrew.secondkill.result.CodeMsg;
import com.andrew.secondkill.util.MD5Util;
import com.andrew.secondkill.util.UUIDUtil;
import com.andrew.secondkill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/21
 */
@Service
public class UserService {

    public static final String COOKIE_TOKEN_NAME ="token";

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserDao userDao;


    public User getUser(long id){
        //先取缓存
        User user = redisService.get(UserKeyPrefix.userIDprefix, "" + id, User.class);
        if(user!=null){
            return user;
        }
        //缓存没有,取数据库
        user = userDao.getUserById(id);
        if(user!=null){
            redisService.set(UserKeyPrefix.userIDprefix,""+id,user);
        }
        return user;
    }

    /**
     *
     * 假设登录时候修改密码场景
     *
     * 更新问题   缓存要保持一致
     * 先更新数据库再删除缓存
     */
    public boolean updatePassword(String token,long id,String formPassword){
        User user = getUser(id);
        if(user==null){
            throw new GlobalException(CodeMsg.SESSION_ERROR);
        }
        //使用try catch 防止数据库更新失败，不用执行后面redis的更改
        try{
            //设置新对象更新数据库比在原对象设置数据库高效->只改password（新写一个sql）
            User newPasswordUser = new User();
            newPasswordUser.setId(id);
            newPasswordUser.setPassword(MD5Util.formPassToDBPass(formPassword,user.getSalt()));
            userDao.updatePasswordById(newPasswordUser);
            //删除id缓存
            redisService.delete(UserKeyPrefix.userIDprefix,""+id);
            user.setPassword(newPasswordUser.getPassword());
            //更新token缓存
            redisService.set(UserKeyPrefix.userTOKENprefix,token,user);
            return true;
        }catch (GlobalException e){
            throw new GlobalException(CodeMsg.PASSWORD_UPDATE_ERROR);
        }



    }

    public User getByToken(HttpServletRequest request, HttpServletResponse response, String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        User user = redisService.get(UserKeyPrefix.userTOKENprefix, token, User.class);
        //每获取一次信息，延长过期时间
        if(user!=null){
            addCookieToClient(token,user,response);
        }

        return user;
    }

    public String login(HttpServletResponse response, LoginVo loginVo){

        if(loginVo==null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        User user = getUser(Long.parseLong(mobile));

        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        String dbSalt = user.getSalt();
        String dbPassword = user.getPassword();
        String calculatePassword = MD5Util.formPassToDBPass(password, dbSalt);
        if(calculatePassword.equals(dbPassword)){

            String uuid = UUIDUtil.uuid();

            addCookieToClient(uuid,user,response);

            return uuid;
        }else{
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
    }

    private void addCookieToClient(String uuid, User user, HttpServletResponse response){

        redisService.set(UserKeyPrefix.userTOKENprefix,uuid,user);
        Cookie cookie = new Cookie(COOKIE_TOKEN_NAME,uuid);
        cookie.setMaxAge(UserKeyPrefix.userTOKENprefix.expireSecond());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
