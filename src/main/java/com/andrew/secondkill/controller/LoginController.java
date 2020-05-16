package com.andrew.secondkill.controller;

import com.andrew.secondkill.result.Result;
import com.andrew.secondkill.service.UserService;
import com.andrew.secondkill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/22
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    UserService userService;

    @RequestMapping("/to_login")
    public String to_login(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> do_login(HttpServletResponse response, @Valid LoginVo loginVo){
        String token =userService.login(response,loginVo);
        return Result.success(token);
    }



}
