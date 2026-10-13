package com.relily.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.relily.system.domain.CosmeticsExpress;
import com.relily.system.mapper.CosmeticsExpressMapper;
import com.relily.system.service.ICosmeticsExpressService;

/**
 * 物流轨迹 服务层实现
 * 
 * @author relily
 */
@Service
public class CosmeticsExpressServiceImpl implements ICosmeticsExpressService {
    @Autowired
    private CosmeticsExpressMapper cosmeticsExpressMapper;

    @Override
    public CosmeticsExpress selectCosmeticsExpressByOrderNo(String orderNo) {
        return cosmeticsExpressMapper.selectCosmeticsExpressByOrderNo(orderNo);
    }

    @Override
    public void upsertCosmeticsExpress(CosmeticsExpress cosmeticsExpress) {
        CosmeticsExpress exist = cosmeticsExpressMapper.selectCosmeticsExpressByOrderNo(cosmeticsExpress.getOrderNo());
        if (exist == null) {
            cosmeticsExpressMapper.insertCosmeticsExpress(cosmeticsExpress);
        } else {
            cosmeticsExpressMapper.updateCosmeticsExpress(cosmeticsExpress);
        }
    }
}