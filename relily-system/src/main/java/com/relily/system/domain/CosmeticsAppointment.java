package com.relily.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.relily.common.core.domain.BaseEntity;
import jakarta.validation.constraints.Min;

/**
 * 预约单对象 cosmetics_order
 * 
 * @author relily
 */
public class CosmeticsAppointment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 预约ID */
    private Long id;

    /** 预约单号(唯一) */
    private String orderNo;

    /** 预约用户ID(买家) */
    private Long userId;

    /** 预约用户名(买家) */
    private String userName;

    /** 预约的商品ID */
    private Long goodsId;

    /** 商品名称(冗余快照) */
    private String goodsName;

    /** 卖家用户ID(冗余,用于授权查询) */
    private Long sellerId;

    /** 预约单价(冗余快照) */
    private BigDecimal unitPrice;

    /** 预约数量 */
    @Min(value = 1, message = "预约数量至少为1")
    private Integer quantity;

    /** 总价(仅预约显示,不支付) */
    private BigDecimal totalAmount;

    /** 预约到店/自取时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date appointTime;

    /** 逾期自动取消的截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /** 联系电话 */
    private String contactPhone;

    /** 收货人 */
    private String receiverName;

    /** 收货联系方式 */
    private String receiverPhone;

    /** 收货地址(线下发快递用) */
    private String receiverAddr;

    /** 状态(0待确认 1已确认 2已取消 3已完成 4已发货) */
    private String status;

    /** 取消原因(含超时自动取消的标记) */
    private String cancelReason;

    /** 取消时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    /** 快递公司 */
    private String expressCompany;

    /** 快递单号 */
    private String expressNo;

    /** 发货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shipTime;

    /** 删除标志(0存在 1删除) */
    private String delFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(Date appointTime) {
        this.appointTime = appointTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddr() {
        return receiverAddr;
    }

    public void setReceiverAddr(String receiverAddr) {
        this.receiverAddr = receiverAddr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public Date getShipTime() {
        return shipTime;
    }

    public void setShipTime(Date shipTime) {
        this.shipTime = shipTime;
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
                .append("orderNo", getOrderNo())
                .append("userId", getUserId())
                .append("userName", getUserName())
                .append("goodsId", getGoodsId())
                .append("goodsName", getGoodsName())
                .append("sellerId", getSellerId())
                .append("unitPrice", getUnitPrice())
                .append("quantity", getQuantity())
                .append("totalAmount", getTotalAmount())
                .append("appointTime", getAppointTime())
                .append("expireTime", getExpireTime())
                .append("contactPhone", getContactPhone())
                .append("receiverName", getReceiverName())
                .append("receiverPhone", getReceiverPhone())
                .append("receiverAddr", getReceiverAddr())
                .append("status", getStatus())
                .append("cancelReason", getCancelReason())
                .append("cancelTime", getCancelTime())
                .append("finishTime", getFinishTime())
                .append("expressCompany", getExpressCompany())
                .append("expressNo", getExpressNo())
                .append("shipTime", getShipTime())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}