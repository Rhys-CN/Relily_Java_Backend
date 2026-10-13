package com.relily.system.service;

import java.util.List;
import com.relily.system.domain.CosmeticsAppointment;

/**
 * 预约单 服务层
 * 
 * @author relily
 */
public interface ICosmeticsAppointmentService {
    /**
     * 查询预约信息
     * 
     * @param id 预约ID
     * @return 预约信息
     */
    public CosmeticsAppointment selectCosmeticsAppointmentById(Long id);

    /**
     * 查询当前用户有权访问的预约详情。
     *
     * @param id 预约ID
     * @param userId 当前用户ID
     * @return 有权访问的预约信息
     */
    public CosmeticsAppointment selectVisibleAppointmentById(Long id, Long userId);

    /**
     * 查询预约列表(买家/管理员视角, 买家自动限定本人)
     * 
     * @param cosmeticsAppointment 预约信息
     * @return 预约集合
     */
    public List<CosmeticsAppointment> selectCosmeticsAppointmentList(CosmeticsAppointment cosmeticsAppointment);

    /**
     * 卖家视角查询自己被预约的单据
     * 
     * @param cosmeticsAppointment 预约信息(携带 sellerId)
     * @return 预约集合
     */
    public List<CosmeticsAppointment> selectAppointmentListForSeller(CosmeticsAppointment cosmeticsAppointment);

    /**
     * 买家下单预约(扣库存/快照/开单号/设时效)
     * 
     * @param cosmeticsAppointment 预约信息
     * @return 结果
     */
    public int insertAppointment(CosmeticsAppointment cosmeticsAppointment);

    /**
     * 修改预约(仅待确认状态, 差额库存同步, 重置时效)
     * 
     * @param cosmeticsAppointment 预约信息
     * @return 结果
     */
    public int updateAppointment(CosmeticsAppointment cosmeticsAppointment);

    /**
     * 买家取消预约(待确认/已确认即可取消, 回补库存)
     * 
     * @param id     预约ID
     * @param userId 操作者ID
     * @return 结果
     */
    public int cancelAppointment(Long id, Long userId);

    /**
     * 卖家确认预约(待确认→已确认)
     * 
     * @param id       预约ID
     * @param sellerId 卖家ID
     * @return 结果
     */
    public int confirmAppointment(Long id, Long sellerId);

    /**
     * 卖家发货(已确认→已发货)
     * 
     * @param appointment 包含快递信息的预约
     * @return 结果
     */
    public int shipAppointment(CosmeticsAppointment appointment);

    /**
     * 签收自动完结(已发货→已完成), 幂等
     * 
     * @param orderNo 预约单号
     * @return 结果
     */
    public int autoComplete(String orderNo);

    /**
     * 原子取消一批超时预约并回补库存。
     *
     * @param now 当前时间
     * @return 成功取消的数量
     */
    public int cancelExpiredAppointments(java.util.Date now);

    /**
     * 删除预约信息
     * 
     * @param ids 需要删除的预约ID
     * @return 结果
     */
    public int deleteCosmeticsAppointmentByIds(Long[] ids);
}
