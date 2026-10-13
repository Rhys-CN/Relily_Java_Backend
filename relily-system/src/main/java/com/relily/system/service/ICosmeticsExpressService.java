package com.relily.system.service;

import com.relily.system.domain.CosmeticsExpress;

/**
 * 物流轨迹 服务层
 * 
 * @author relily
 */
public interface ICosmeticsExpressService {
    /**
     * 按预约单号查询轨迹快照
     * 
     * @param orderNo 预约单号
     * @return 物流轨迹
     */
    public CosmeticsExpress selectCosmeticsExpressByOrderNo(String orderNo);

    /**
     * 保存/更新轨迹快照
     * 
     * @param cosmeticsExpress 物流轨迹
     */
    public void upsertCosmeticsExpress(CosmeticsExpress cosmeticsExpress);
}