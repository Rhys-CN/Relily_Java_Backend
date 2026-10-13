package com.relily.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.relily.common.exception.ServiceException;
import com.relily.common.utils.SecurityUtils;
import com.relily.system.domain.CosmeticsProduct;
import com.relily.system.mapper.CosmeticsProductMapper;
import com.relily.system.service.ICosmeticsProductService;

/**
 * 二手化妆品商品 服务层实现
 * 
 * @author relily
 */
@Service
public class CosmeticsProductServiceImpl implements ICosmeticsProductService {
    @Autowired
    private CosmeticsProductMapper cosmeticsProductMapper;

    @Override
    public CosmeticsProduct selectCosmeticsProductById(Long id) {
        return cosmeticsProductMapper.selectCosmeticsProductById(id);
    }

    @Override
    public CosmeticsProduct selectVisibleCosmeticsProductById(Long id) {
        CosmeticsProduct product = cosmeticsProductMapper.selectCosmeticsProductById(id);
        if (product == null || "1".equals(product.getDelFlag())) {
            return null;
        }
        if (SecurityUtils.isAdmin()) {
            return product;
        }
        if (SecurityUtils.hasRole("seller")) {
            if (SecurityUtils.getUserId().equals(product.getSellerId())) {
                return product;
            }
            throw new ServiceException("无权查看该商品");
        }
        if ("0".equals(product.getStatus())) {
            return product;
        }
        throw new ServiceException("无权查看该商品");
    }

    @Override
    public List<CosmeticsProduct> selectCosmeticsProductList(CosmeticsProduct cosmeticsProduct) {
        return cosmeticsProductMapper.selectCosmeticsProductList(cosmeticsProduct);
    }

    @Override
    public int insertCosmeticsProduct(CosmeticsProduct cosmeticsProduct) {
        return cosmeticsProductMapper.insertCosmeticsProduct(cosmeticsProduct);
    }

    @Override
    public int updateCosmeticsProduct(CosmeticsProduct cosmeticsProduct) {
        // 乐观锁更新: 仅当版本号一致时生效, 防止并发编辑丢更新
        return cosmeticsProductMapper.updateCosmeticsProductByVersion(cosmeticsProduct);
    }

    @Override
    public int deleteCosmeticsProductByIds(Long[] ids) {
        return cosmeticsProductMapper.deleteCosmeticsProductByIds(ids);
    }

    @Override
    public boolean checkSellerOwn(Long id, Long userId) {
        CosmeticsProduct product = cosmeticsProductMapper.selectCosmeticsProductById(id);
        return product != null && userId.equals(product.getSellerId());
    }
}
