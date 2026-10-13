package com.relily.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.relily.system.domain.CosmeticsProduct;

/**
 * 二手化妆品商品 数据层
 * 
 * @author relily
 */
public interface CosmeticsProductMapper {
    /**
     * 查询商品信息
     * 
     * @param id 商品ID
     * @return 商品信息
     */
    public CosmeticsProduct selectCosmeticsProductById(Long id);

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
     * 修改商品
     * 
     * @param cosmeticsProduct 商品信息
     * @return 结果
     */
    public int updateCosmeticsProduct(CosmeticsProduct cosmeticsProduct);

    /**
     * 乐观锁修改商品(version 校验,防止并发编辑丢更新)
     * 
     * @param cosmeticsProduct 商品信息
     * @return 结果
     */
    public int updateCosmeticsProductByVersion(CosmeticsProduct cosmeticsProduct);

    /**
     * 删除商品信息
     * 
     * @param id 商品ID
     * @return 结果
     */
    public int deleteCosmeticsProductById(Long id);

    /**
     * 批量删除商品信息
     * 
     * @param ids 需要删除的商品ID
     * @return 结果
     */
    public int deleteCosmeticsProductByIds(Long[] ids);

    /**
     * 原子扣减库存(乐观锁: status=上架 且 stock>=quantity 才更新)
     * 
     * @param goodsId  商品ID
     * @param quantity 扣减数量
     * @return 影响行数(0表示库存不足或已下架)
     */
    public int deductStock(@Param("goodsId") Long goodsId, @Param("quantity") int quantity);

    /**
     * 回补库存
     * 
     * @param goodsId  商品ID
     * @param quantity 回补数量
     * @return 影响行数
     */
    public int rollbackStock(@Param("goodsId") Long goodsId, @Param("quantity") int quantity);
}
