package com.relily.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.relily.common.annotation.Log;
import com.relily.common.core.controller.BaseController;
import com.relily.common.core.domain.AjaxResult;
import com.relily.common.core.page.TableDataInfo;
import com.relily.common.enums.BusinessType;
import com.relily.common.utils.SecurityUtils;
import com.relily.system.domain.CosmeticsProduct;
import com.relily.system.service.ICosmeticsProductService;

/**
 * 二手化妆品商品 信息操作处理
 * 
 * @author relily
 */
@RestController
@RequestMapping("/system/cosmeticsProduct")
public class CosmeticsProductController extends BaseController {
    @Autowired
    private ICosmeticsProductService cosmeticsProductService;

    /**
     * 查询商品列表(按角色隔离数据)
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(CosmeticsProduct product) {
        startPage();
        if (!SecurityUtils.hasRole("admin")) {
            if (SecurityUtils.hasRole("seller")) {
                // 卖家: 只能看自己上架的商品
                product.setSellerId(SecurityUtils.getUserId());
            } else {
                // 普通买家: 只看上架中的商品
                product.setStatus("0");
            }
        }
        List<CosmeticsProduct> list = cosmeticsProductService.selectCosmeticsProductList(product);
        return getDataTable(list);
    }

    /**
     * 获取商品详细信息
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:product:list')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        CosmeticsProduct product = cosmeticsProductService.selectVisibleCosmeticsProductById(id);
        return product == null ? error("商品不存在") : success(product);
    }

    /**
     * 新增商品(卖家/管理员)
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:product:add')")
    @Log(title = "化妆品商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CosmeticsProduct product) {
        product.setSellerId(SecurityUtils.getUserId());
        product.setSellerName(SecurityUtils.getUsername());
        product.setCreateBy(SecurityUtils.getUsername());
        if (product.getStatus() == null) {
            product.setStatus("0");
        }
        if (product.getDelFlag() == null) {
            product.setDelFlag("0");
        }
        return toAjax(cosmeticsProductService.insertCosmeticsProduct(product));
    }

    /**
     * 修改商品(乐观锁, 仅本人/管理员)
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:product:edit')")
    @Log(title = "化妆品商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CosmeticsProduct product) {
        if (!SecurityUtils.isAdmin()
                && !cosmeticsProductService.checkSellerOwn(product.getId(), SecurityUtils.getUserId())) {
            return error("无权修改该商品");
        }
        product.setUpdateBy(SecurityUtils.getUsername());
        // 乐观锁更新: 若版本号不匹配说明他人已修改, 返回0
        int rows = cosmeticsProductService.updateCosmeticsProduct(product);
        if (rows == 0) {
            return error("修改失败: 商品已被他人修改, 请刷新后重试");
        }
        return success();
    }

    /**
     * 上架/下架商品
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:product:edit')")
    @Log(title = "化妆品商品", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody CosmeticsProduct product) {
        if (product.getId() == null) {
            return error("商品ID不能为空");
        }
        if (!SecurityUtils.isAdmin()
                && !cosmeticsProductService.checkSellerOwn(product.getId(), SecurityUtils.getUserId())) {
            return error("无权操作该商品");
        }
        CosmeticsProduct db = cosmeticsProductService.selectCosmeticsProductById(product.getId());
        if (db != null) {
            db.setStatus(product.getStatus());
            db.setUpdateBy(SecurityUtils.getUsername());
            return toAjax(cosmeticsProductService.updateCosmeticsProduct(db));
        }
        return error("商品不存在");
    }

    /**
     * 删除商品(软删除)
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:product:remove')")
    @Log(title = "化妆品商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(cosmeticsProductService.deleteCosmeticsProductByIds(ids));
    }
}
