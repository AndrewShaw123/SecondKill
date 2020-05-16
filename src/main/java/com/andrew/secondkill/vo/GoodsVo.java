package com.andrew.secondkill.vo;

import com.andrew.secondkill.domain.Goods;

import java.util.Date;

/**
 * GoodsVo Class
 *
 * @author andrew
 * @date 2020/3/24
 */
public class GoodsVo extends Goods {
    private Double killPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Double getKillPrice() {
        return killPrice;
    }

    public void setKillPrice(Double killPrice) {
        this.killPrice = killPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
