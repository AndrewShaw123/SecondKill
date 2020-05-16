package com.andrew.secondkill.access;

import com.andrew.secondkill.domain.User;

/**
 * UserContext Class
 *
 * @author andrew
 * @date 2020/4/4
 */
public class UserContext {

    private static ThreadLocal<User> localUser = new ThreadLocal<>();

    public static void setLocalUser(User user){
        localUser.set(user);
    }

    public static User getLocalUser(){
        return localUser.get();
    }

}
