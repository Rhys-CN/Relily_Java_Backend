package com.relily.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.relily.common.core.domain.BaseEntity;

/**
 * 物流轨迹对象 cosmetics_express
 * 
 * @author relily
 */
public class CosmeticsExpress extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 轨迹ID */
    private Long id;

    /** 预约单ID */
    private Long appointmentId;

    /** 预约单号 */
    private String orderNo;

    /** 快递公司 */
    private String expressCompany;

    /** 快递单号 */
    private String expressNo;

    /** 物流状态(0收件/揽收 1在途中 2派送中 3已签收) */
    private String trackStatus;

    /** 是否已签收(0否 1是) */
    private String isSigned;

    /** 轨迹详情(JSON) */
    private String trackInfo;

    /** 最近一次查询时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastQueryTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public String getTrackStatus() {
        return trackStatus;
    }

    public void setTrackStatus(String trackStatus) {
        this.trackStatus = trackStatus;
    }

    public String getIsSigned() {
        return isSigned;
    }

    public void setIsSigned(String isSigned) {
        this.isSigned = isSigned;
    }

    public String getTrackInfo() {
        return trackInfo;
    }

    public void setTrackInfo(String trackInfo) {
        this.trackInfo = trackInfo;
    }

    public Date getLastQueryTime() {
        return lastQueryTime;
    }

    public void setLastQueryTime(Date lastQueryTime) {
        this.lastQueryTime = lastQueryTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("appointmentId", getAppointmentId())
                .append("orderNo", getOrderNo())
                .append("expressCompany", getExpressCompany())
                .append("expressNo", getExpressNo())
                .append("trackStatus", getTrackStatus())
                .append("isSigned", getIsSigned())
                .append("trackInfo", getTrackInfo())
                .append("lastQueryTime", getLastQueryTime())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}