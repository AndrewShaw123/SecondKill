package com.andrew.secondkill.rabbitmq;

import com.andrew.secondkill.domain.User;

/**
 * KillMessage Class
 *
 * @author andrew
 * @date 2020/4/2
 */
public class KillMessage {
    private User user;
    private long goodsId;

    public KillMessage(User user, long goodsId) {
        this.user = user;
        this.goodsId = goodsId;
    }

    public KillMessage() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    @Override
    public String toString() {
        return "KillMessage{" +
                "user=" + user +
                ", goodsId=" + goodsId +
                '}';
    }
}
