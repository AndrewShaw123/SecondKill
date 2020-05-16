package com.andrew.secondkill.controller;

import com.andrew.secondkill.domain.User;
import com.andrew.secondkill.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * UserController Class
 *
 * @author andrew
 * @date 2020/3/29
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public Result<User> info(User user){
        return Result.success(user);
    }

}
