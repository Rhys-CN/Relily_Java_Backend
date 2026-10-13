package com.relily.system.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.relily.system.domain.CosmeticsAppointment;

/**
 * 预约单 数据层
 * 
 * @author relily
 */
public interface CosmeticsAppointmentMapper {
    /**
     * 查询预约信息
     * 
     * @param id 预约ID
     * @return 预约信息
     */
    public CosmeticsAppointment selectCosmeticsAppointmentById(Long id);

    /**
     * 按预约单号查询预约信息
     * 
     * @param orderNo 预约单号
     * @return 预约信息
     */
    public CosmeticsAppointment selectCosmeticsAppointmentByOrderNo(String orderNo);

    /**
     * 查询预约列表
     * 
     * @param cosmeticsAppointment 预约信息
     * @return 预约集合
     */
    public List<CosmeticsAppointment> selectCosmeticsAppointmentList(CosmeticsAppointment cosmeticsAppointment);

    /**
     * 卖家视角查询自己被预约的单据(JOIN 商品表防止越权)
     * 
     * @param cosmeticsAppointment 预约信息(需携带 sellerId)
     * @return 预约集合
     */
    public List<CosmeticsAppointment> selectAppointmentListForSeller(CosmeticsAppointment cosmeticsAppointment);

    /**
     * 新增预约
     * 
     * @param cosmeticsAppointment 预约信息
     * @return 结果
     */
    public int insertCosmeticsAppointment(CosmeticsAppointment cosmeticsAppointment);

    /**
     * 修改预约
     * 
     * @param cosmeticsAppointment 预约信息
     * @return 结果
     */
    public int updateCosmeticsAppointment(CosmeticsAppointment cosmeticsAppointment);

    /**
     * 删除预约信息
     * 
     * @param id 预约ID
     * @return 结果
     */
    public int deleteCosmeticsAppointmentById(Long id);

    /**
     * 批量删除预约信息
     * 
     * @param ids 需要删除的预约ID
     * @return 结果
     */
    public int deleteCosmeticsAppointmentByIds(Long[] ids);

    /**
     * 原子取消指定状态的预约单。
     * 
     * @param appointment 预约信息
     * @return 更新行数
     */
    public int cancelAppointmentIfPending(CosmeticsAppointment appointment);

    /**
     * 查询一批待确认且已超时的预约详情。
     *
     * @param now 当前时间
     * @param limit 批处理上限
     * @return 超时预约集合
     */
    public List<CosmeticsAppointment> selectExpiredAppointments(@Param("now") Date now, @Param("limit") int limit);
}
