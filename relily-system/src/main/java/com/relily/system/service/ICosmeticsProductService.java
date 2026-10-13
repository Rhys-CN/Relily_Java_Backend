package com.relily.system.service;

import java.util.List;
import com.relily.system.domain.CosmeticsProduct;

/**
 * 二手化妆品商品 服务层
 * 
 * @author relily
 */
public interface ICosmeticsProductService {
    /**
     * 查询商品信息
     * 
     * @param id 商品ID
     * @return 商品信息
     */
    public CosmeticsProduct selectCosmeticsProductById(Long id);

    /**
     * 查询当前用户可见的商品。管理员可查看全部，卖家仅可查看自己的商品，买家仅可查看上架商品。
     *
     * @param id 商品ID
     * @return 商品信息
     */
    public CosmeticsProduct selectVisibleCosmeticsProductById(Long id);

    /**
     * 查询商品列表
     * 
     * @param cosmeticsProduct 商品信息
     * @return 商品集合
     */
    public List<CosmeticsProduct> selectCosmeticsProductList(CosmeticsProduct cosmeticsProduct);

    /**
     * 新增商品
     * 
     * @param cosmeticsProduct 商品信息
     * @return 结果
     */
    public int insertCosmeticsProduct(CosmeticsProduct cosmeticsProduct);

    /**
     * 修改商品(乐观锁)
     * 
     * @param cosmeticsProduct 商品信息
     * @return 结果
     */
    public int updateCosmeticsProduct(CosmeticsProduct cosmeticsProduct);

    /**
     * 删除商品信息(物理删除仅限管理员)
     * 
     * @param ids 需要删除的商品ID
     * @return 结果
     */
    public int deleteCosmeticsProductByIds(Long[] ids);

    /**
     * 校验商品是否属于当前卖家
     * 
     * @param id     商品ID
     * @param userId 用户ID
     * @return true 可操作
     */
    public boolean checkSellerOwn(Long id, Long userId);
}
