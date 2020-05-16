package com.andrew.secondkill.vo;

import com.andrew.secondkill.domain.User;

/**
 * GoodsDetailVo Class
 *
 * @author andrew
 * @date 2020/3/31
 */
public class GoodsDetailVo {

    private GoodsVo goodsVo;
    private User user;
    private int remainSecond;
    private int status;

    public GoodsDetailVo(){

    }

    public GoodsDetailVo(GoodsVo goodsVo, User user, int remainSecond, int status) {
        this.goodsVo = goodsVo;
        this.user = user;
        this.remainSecond = remainSecond;
        this.status = status;
    }

    public int getRemainSecond() {
        return remainSecond;
    }

    public void setRemainSecond(int remainSecond) {
        this.remainSecond = remainSecond;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
