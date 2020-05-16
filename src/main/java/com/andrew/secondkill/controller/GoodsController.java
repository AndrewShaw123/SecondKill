package com.andrew.secondkill.controller;

import com.andrew.secondkill.domain.User;
import com.andrew.secondkill.redis.GoodsKeyPrefix;
import com.andrew.secondkill.redis.RedisService;
import com.andrew.secondkill.result.Result;
import com.andrew.secondkill.service.GoodsService;
import com.andrew.secondkill.service.UserService;
import com.andrew.secondkill.vo.GoodsDetailVo;
import com.andrew.secondkill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/23
 */
@Controller
@RequestMapping("/goods")
@SuppressWarnings("all")
public class GoodsController {

    @Autowired
    UserService userService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 优化前：738吞吐量
     * 5000*10个并发
     *
     * 添加页面缓存
     * 优化后：1727吞吐量
     * 5000*10个并发
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value="/to_list",produces = "text/html")
    @ResponseBody
    public String to_list(HttpServletRequest request, HttpServletResponse response,Model model, User user){

        //取页面缓存
        String html = redisService.get(GoodsKeyPrefix.goodsLISTprefix, "", String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsList);

        //手动渲染thymeleaf，然后设置页面缓存
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKeyPrefix.goodsLISTprefix,"",html);
        }

        return html;

        /*return "goods_list";*/
    }

    /**
     * 这里使用页面缓存，缓存页面放到redis里
     * 已经不用
     */
    @RequestMapping(value = "/to_detail2/{goodsId}",produces="text/html")
    @ResponseBody
    public String to_detail2(HttpServletRequest request,HttpServletResponse response,@PathVariable("goodsId")long goodsId,Model model,User user){

        //取页面缓存
        String html = redisService.get(GoodsKeyPrefix.goodsDETAILprefix, "" + goodsId, String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        GoodsVo goodsVo = goodsService.detailGoodsVo(goodsId);

        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int status;
        int remainSecond;

        /*未开始*/
        if(now < startTime){
            status = 0;
            remainSecond = (int)(startTime - now)/1000;
        }/*结束*/
        else if(now > endTime){
            status = 2;
            remainSecond = -1;
        }else{//进行中
            status = 1;
            remainSecond = 0;
        }

        model.addAttribute("status",status);
        model.addAttribute("remainSecond",remainSecond);
        model.addAttribute("user",user);
        model.addAttribute("goodsVo",goodsVo);

        /*return "goods_detail";*/

        //手动渲染thymeleaf，然后设置页面缓存
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", webContext);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKeyPrefix.goodsDETAILprefix,""+goodsId,html);
        }
        return html;
    }

    /**
     * 一般使用页面静态化 让客户端浏览器缓存页面 304 If-modify-since
     * 使用ajax 异步请求数据
     */
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> to_detail(@PathVariable("goodsId")long goodsId, User user){

        GoodsVo goodsVo = goodsService.detailGoodsVo(goodsId);
        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int status;
        int remainSecond;

        /*未开始*/
        if(now < startTime){
            status = 0;
            remainSecond = (int)(startTime - now)/1000;
        }/*结束*/
        else if(now > endTime){
            status = 2;
            remainSecond = -1;
        }else{//进行中
            status = 1;
            remainSecond = 0;
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo(goodsVo,user,remainSecond,status);
        return  Result.success(goodsDetailVo);
    }



}
