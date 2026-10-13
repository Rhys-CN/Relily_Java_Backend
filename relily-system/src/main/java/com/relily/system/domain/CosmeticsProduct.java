package com.relily.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.relily.common.core.domain.BaseEntity;
import com.relily.common.xss.Xss;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 二手化妆品商品对象 cosmetics_product
 * 
 * @author relily
 */
public class CosmeticsProduct extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long id;

    /** 乐观锁版本号 */
    private Long version;

    /** 商品名称 */
    @NotBlank(message = "商品名称不能为空")
    @Size(min = 0, max = 100, message = "商品名称长度不能超过100个字符")
    private String goodsName;

    /** 分类(洁面/面霜/乳液/口红/香水...) */
    private String category;

    /** 品牌 */
    private String brand;

    /** 单价(元) */
    @DecimalMin(value = "0", message = "单价不能小于0")
    private BigDecimal price;

    /** 余量/库存 */
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock;

    /** 成色(字典:cosm_condition_level) */
    private String conditionLevel;

    /** 主图地址 */
    private String image;

    /** 说明详情/规格/使用情况 */
    @Xss(message = "说明详情不能包含脚本字符")
    private String description;

    /** 注意事项(过敏提醒/过期提醒等) */
    @Xss(message = "注意事项不能包含脚本字符")
    private String notice;

    /** 状态(0上架 1下架) */
    private String status;

    /** 卖家用户ID */
    private Long sellerId;

    /** 卖家用户名 */
    private String sellerName;

    /** 删除标志(0存在 1删除) */
    private String delFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getConditionLevel() {
        return conditionLevel;
    }

    public void setConditionLevel(String conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("version", getVersion())
                .append("goodsName", getGoodsName())
                .append("category", getCategory())
                .append("brand", getBrand())
                .append("price", getPrice())
                .append("stock", getStock())
                .append("conditionLevel", getConditionLevel())
                .append("image", getImage())
                .append("description", getDescription())
                .append("notice", getNotice())
                .append("status", getStatus())
                .append("sellerId", getSellerId())
                .append("sellerName", getSellerName())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
