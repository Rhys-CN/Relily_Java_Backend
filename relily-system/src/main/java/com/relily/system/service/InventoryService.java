package com.relily.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.relily.system.mapper.CosmeticsProductMapper;

/**
 * 库存服务: 统一扣减/回补库存, 供下单、取消、修改预约复用
 * 
 * @author relily
 */
@Service
public class InventoryService {
    @Autowired
    private CosmeticsProductMapper productMapper;

    /**
     * 扣减库存(乐观锁: 上架状态 且 stock >= qty 才原子 UPDATE), 返回0表示库存不足
     * 
     * @param goodsId 商品ID
     * @param qty     扣减数量
     * @return 影响行数
     */
    public int deductStock(Long goodsId, int qty) {
        return productMapper.deductStock(goodsId, qty);
    }

    /**
     * 回补库存
     * 
     * @param goodsId 商品ID
     * @param qty     回补数量
     * @return 影响行数
     */
    public int rollbackStock(Long goodsId, int qty) {
        return productMapper.rollbackStock(goodsId, qty);
    }
}