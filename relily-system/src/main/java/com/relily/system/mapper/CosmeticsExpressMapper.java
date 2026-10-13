package com.relily.system.mapper;

import com.relily.system.domain.CosmeticsExpress;

/**
 * 物流轨迹 数据层
 * 
 * @author relily
 */
public interface CosmeticsExpressMapper {
    /**
     * 按预约单号查询轨迹
     * 
     * @param orderNo 预约单号
     * @return 物流轨迹
     */
    public CosmeticsExpress selectCosmeticsExpressByOrderNo(String orderNo);

    /**
     * 新增轨迹
     * 
     * @param cosmeticsExpress 物流轨迹
     * @return 结果
     */
    public int insertCosmeticsExpress(CosmeticsExpress cosmeticsExpress);

    /**
     * 修改轨迹
     * 
     * @param cosmeticsExpress 物流轨迹
     * @return 结果
     */
    public int updateCosmeticsExpress(CosmeticsExpress cosmeticsExpress);
}