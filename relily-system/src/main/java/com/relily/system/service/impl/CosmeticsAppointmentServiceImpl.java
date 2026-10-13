package com.relily.system.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.relily.common.core.domain.entity.SysUser;
import com.relily.common.exception.ServiceException;
import com.relily.common.utils.DateUtils;
import com.relily.common.utils.SecurityUtils;
import com.relily.common.utils.StringUtils;
import com.relily.common.utils.uuid.Seq;
import com.relily.system.domain.CosmeticsAppointment;
import com.relily.system.domain.CosmeticsProduct;
import com.relily.system.mapper.CosmeticsAppointmentMapper;
import com.relily.system.service.ICosmeticsAppointmentService;
import com.relily.system.service.ICosmeticsProductService;
import com.relily.system.service.InventoryService;

/**
 * 预约单 服务层实现
 * 
 * @author relily
 */
@Service
public class CosmeticsAppointmentServiceImpl implements ICosmeticsAppointmentService {
    /** 待确认超时小时数 */
    private static final int EXPIRE_HOURS = 48;

    @Autowired
    private CosmeticsAppointmentMapper cosmeticsAppointmentMapper;

    @Autowired
    private ICosmeticsProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Override
    public CosmeticsAppointment selectCosmeticsAppointmentById(Long id) {
        return cosmeticsAppointmentMapper.selectCosmeticsAppointmentById(id);
    }

    @Override
    public CosmeticsAppointment selectVisibleAppointmentById(Long id, Long userId) {
        CosmeticsAppointment appointment = cosmeticsAppointmentMapper.selectCosmeticsAppointmentById(id);
        if (appointment == null || "1".equals(appointment.getDelFlag())) {
            return null;
        }
        if (SecurityUtils.isAdmin() || userId.equals(appointment.getUserId()) || userId.equals(appointment.getSellerId())) {
            return appointment;
        }
        throw new ServiceException("无权查看该预约单");
    }

    @Override
    public List<CosmeticsAppointment> selectCosmeticsAppointmentList(CosmeticsAppointment cosmeticsAppointment) {
        return cosmeticsAppointmentMapper.selectCosmeticsAppointmentList(cosmeticsAppointment);
    }

    @Override
    public List<CosmeticsAppointment> selectAppointmentListForSeller(CosmeticsAppointment cosmeticsAppointment) {
        return cosmeticsAppointmentMapper.selectAppointmentListForSeller(cosmeticsAppointment);
    }

    @Override
    @Transactional
    public int insertAppointment(CosmeticsAppointment apt) {
        // 1. 取当前登录用户(买家)
        SysUser user = SecurityUtils.getLoginUser().getUser();
        apt.setUserId(user.getUserId());
        apt.setUserName(user.getNickName());
        apt.setCreateBy(user.getUserName());
        apt.setStatus("0");

        // 2. 读取商品并校验
        CosmeticsProduct goods = productService.selectCosmeticsProductById(apt.getGoodsId());
        if (goods == null || "1".equals(goods.getStatus()) || "1".equals(goods.getDelFlag())) {
            throw new ServiceException("商品不可用或已下架");
        }

        // 3. 校验收货信息必填(线下发快递)
        if (StringUtils.isAnyBlank(apt.getReceiverName(), apt.getReceiverPhone(), apt.getReceiverAddr())) {
            throw new ServiceException("请填写收货人、收货联系方式、收货地址");
        }

        // 4. 快照价格/卖家、生成单号、设置过期时间(48小时后自动取消)
        apt.setGoodsName(goods.getGoodsName());
        apt.setSellerId(goods.getSellerId());
        apt.setUnitPrice(goods.getPrice());
        int quantity = apt.getQuantity() == null ? 1 : apt.getQuantity();
        apt.setQuantity(quantity);
        // BigDecimal 安全计算避免精度丢失
        apt.setTotalAmount(goods.getPrice().multiply(BigDecimal.valueOf(quantity)));
        apt.setOrderNo(buildOrderNo());
        apt.setExpireTime(addHours(new Date(), EXPIRE_HOURS));

        // 5. 扣减库存(乐观锁, 返回0表示库存不足)
        if (inventoryService.deductStock(apt.getGoodsId(), quantity) == 0) {
            throw new ServiceException("库存不足，下单失败");
        }

        return cosmeticsAppointmentMapper.insertCosmeticsAppointment(apt);
    }

    @Override
    @Transactional
    public int updateAppointment(CosmeticsAppointment apt) {
        if (apt.getId() == null) {
            throw new ServiceException("预约ID不能为空");
        }
        CosmeticsAppointment db = cosmeticsAppointmentMapper.selectCosmeticsAppointmentById(apt.getId());
        // 权限: 仅本人可修改
        if (db == null || !SecurityUtils.getUserId().equals(db.getUserId())) {
            throw new ServiceException("无权操作该预约单");
        }
        // 严格限制时机: 仅待确认(0)允许修改
        if (!"0".equals(db.getStatus())) {
            throw new ServiceException("仅待确认状态的预约可修改");
        }

        // 可改字段(忽略传入的其他字段)
        apt.setUserId(db.getUserId());
        apt.setGoodsId(db.getGoodsId());
        apt.setGoodsName(db.getGoodsName());
        apt.setSellerId(db.getSellerId());
        apt.setUnitPrice(db.getUnitPrice());

        int newQuantity = apt.getQuantity() == null ? db.getQuantity() : apt.getQuantity();
        if (newQuantity < 1) {
            throw new ServiceException("预约数量至少为1");
        }

        // 数量差额库存调整
        int diff = newQuantity - db.getQuantity();
        if (diff > 0) {
            if (inventoryService.deductStock(db.getGoodsId(), diff) == 0) {
                throw new ServiceException("库存不足，无法增加预约数量");
            }
        } else if (diff < 0) {
            inventoryService.rollbackStock(db.getGoodsId(), -diff);
        }

        apt.setQuantity(newQuantity);
        apt.setTotalAmount(db.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));

        // 重置时效: 修改了数量或时间则重新延后过期时间
        apt.setExpireTime(addHours(new Date(), EXPIRE_HOURS));
        apt.setUpdateBy(SecurityUtils.getUsername());

        return cosmeticsAppointmentMapper.updateCosmeticsAppointment(apt);
    }

    @Override
    @Transactional
    public int cancelAppointment(Long id, Long userId) {
        CosmeticsAppointment db = cosmeticsAppointmentMapper.selectCosmeticsAppointmentById(id);
        if (db == null) {
            throw new ServiceException("预约单不存在");
        }
        // 本人、所属卖家或管理员可取消
        if (!SecurityUtils.isAdmin() && !userId.equals(db.getUserId()) && !userId.equals(db.getSellerId())) {
            throw new ServiceException("无权取消该预约单");
        }
        if (!"0".equals(db.getStatus()) && !"1".equals(db.getStatus())) {
            throw new ServiceException("当前状态不可取消");
        }

        CosmeticsAppointment cancellation = new CosmeticsAppointment();
        cancellation.setId(db.getId());
        cancellation.setCancelReason("用户取消");
        cancellation.setCancelTime(new Date());
        cancellation.setUpdateBy(SecurityUtils.getUsername());
        int rows = cosmeticsAppointmentMapper.cancelAppointmentIfPending(cancellation);

        // 仅实际完成状态迁移的请求回补库存，避免并发取消重复回补。
        if (rows == 1) {
            inventoryService.rollbackStock(db.getGoodsId(), db.getQuantity());
        }
        return rows;
    }

    @Override
    @Transactional
    public int confirmAppointment(Long id, Long sellerId) {
        CosmeticsAppointment db = cosmeticsAppointmentMapper.selectCosmeticsAppointmentById(id);
        if (db == null) {
            throw new ServiceException("预约单不存在");
        }
        if (!"0".equals(db.getStatus())) {
            throw new ServiceException("仅待确认状态可确认");
        }
        // 仅商品所属卖家或管理员可确认
        if (!SecurityUtils.isAdmin() && !sellerId.equals(db.getSellerId())) {
            throw new ServiceException("无权确认该预约单");
        }

        db.setStatus("1");
        db.setExpireTime(null);
        db.setUpdateBy(SecurityUtils.getUsername());
        return cosmeticsAppointmentMapper.updateCosmeticsAppointment(db);
    }

    @Override
    @Transactional
    public int shipAppointment(CosmeticsAppointment apt) {
        CosmeticsAppointment db = cosmeticsAppointmentMapper.selectCosmeticsAppointmentById(apt.getId());
        if (db == null) {
            throw new ServiceException("预约单不存在");
        }
        if (!"1".equals(db.getStatus())) {
            throw new ServiceException("仅已确认状态可发货");
        }
        // 仅商品所属卖家或管理员可发货
        if (!SecurityUtils.isAdmin() && !SecurityUtils.getUserId().equals(db.getSellerId())) {
            throw new ServiceException("无权发货该预约单");
        }
        if (StringUtils.isAnyBlank(apt.getExpressCompany(), apt.getExpressNo())) {
            throw new ServiceException("请填写快递公司与快递单号");
        }

        db.setStatus("4");
        db.setExpressCompany(apt.getExpressCompany());
        db.setExpressNo(apt.getExpressNo());
        db.setShipTime(new Date());
        db.setUpdateBy(SecurityUtils.getUsername());
        return cosmeticsAppointmentMapper.updateCosmeticsAppointment(db);
    }

    @Override
    @Transactional
    public int autoComplete(String orderNo) {
        CosmeticsAppointment db = cosmeticsAppointmentMapper.selectCosmeticsAppointmentByOrderNo(orderNo);
        if (db == null || !"4".equals(db.getStatus())) {
            // 幂等: 非已发货状态直接忽略
            return 0;
        }
        db.setStatus("3");
        db.setFinishTime(new Date());
        db.setUpdateBy("system");
        return cosmeticsAppointmentMapper.updateCosmeticsAppointment(db);
    }

    @Override
    @Transactional
    public int cancelExpiredAppointments(Date now) {
        int cancelled = 0;
        while (true) {
            List<CosmeticsAppointment> appointments = cosmeticsAppointmentMapper.selectExpiredAppointments(now, 100);
            if (appointments.isEmpty()) {
                return cancelled;
            }
            for (CosmeticsAppointment appointment : appointments) {
                CosmeticsAppointment cancellation = new CosmeticsAppointment();
                cancellation.setId(appointment.getId());
                cancellation.setCancelReason("超时自动取消");
                cancellation.setCancelTime(now);
                cancellation.setUpdateBy("system");
                if (cosmeticsAppointmentMapper.cancelAppointmentIfPending(cancellation) == 1) {
                    inventoryService.rollbackStock(appointment.getGoodsId(), appointment.getQuantity());
                    cancelled++;
                }
            }
        }
    }

    @Override
    public int deleteCosmeticsAppointmentByIds(Long[] ids) {
        return cosmeticsAppointmentMapper.deleteCosmeticsAppointmentByIds(ids);
    }

    /**
     * 生成预约单号: yyyyMMddHHmmss + 6位随机
     */
    private String buildOrderNo() {
        return DateUtils.parseDateToStr("yyyyMMddHHmmss", new Date()) + Seq.getId();
    }

    /**
     * 当前时间延后指定小时
     */
    private Date addHours(Date date, int hours) {
        return new Date(date.getTime() + hours * 3600L * 1000L);
    }
}
