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
import com.relily.system.domain.CosmeticsAppointment;
import com.relily.system.service.ICosmeticsAppointmentService;
import com.relily.system.service.LogisticsService;

/**
 * 预约单 信息操作处理
 * 
 * @author relily
 */
@RestController
@RequestMapping("/system/cosmeticsAppointment")
public class CosmeticsAppointmentController extends BaseController {
    @Autowired
    private ICosmeticsAppointmentService cosmeticsAppointmentService;

    @Autowired
    private LogisticsService logisticsService;

    /**
     * 查询预约列表(按角色隔离数据)
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:appointment:list')")
    @GetMapping("/list")
    public TableDataInfo list(CosmeticsAppointment appointment) {
        startPage();
        List<CosmeticsAppointment> list;
        if (SecurityUtils.hasRole("admin")) {
            // 管理员: 全量
            list = cosmeticsAppointmentService.selectCosmeticsAppointmentList(appointment);
        } else if (SecurityUtils.hasRole("seller")) {
            // 卖家: 只查自己被预约的单据
            appointment.setSellerId(SecurityUtils.getUserId());
            list = cosmeticsAppointmentService.selectAppointmentListForSeller(appointment);
        } else {
            // 普通买家: 只看自己的预约
            appointment.setUserId(SecurityUtils.getUserId());
            list = cosmeticsAppointmentService.selectCosmeticsAppointmentList(appointment);
        }
        return getDataTable(list);
    }

    /**
     * 获取预约详细信息
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:appointment:list')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        CosmeticsAppointment appointment = cosmeticsAppointmentService.selectVisibleAppointmentById(id,
                SecurityUtils.getUserId());
        return appointment == null ? error("预约单不存在") : success(appointment);
    }

    /**
     * 买家下单预约
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:appointment:add')")
    @Log(title = "化妆商品预约", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CosmeticsAppointment appointment) {
        return toAjax(cosmeticsAppointmentService.insertAppointment(appointment));
    }

    /**
     * 修改预约(买家, 仅待确认状态)
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:appointment:edit')")
    @Log(title = "化妆商品预约", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CosmeticsAppointment appointment) {
        return toAjax(cosmeticsAppointmentService.updateAppointment(appointment));
    }

    /**
     * 取消预约(买家/管理员)
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:appointment:cancel')")
    @Log(title = "化妆商品预约", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel")
    public AjaxResult cancel(@RequestBody CosmeticsAppointment appointment) {
        if (appointment.getId() == null) {
            return error("预约ID不能为空");
        }
        return toAjax(cosmeticsAppointmentService.cancelAppointment(appointment.getId(), SecurityUtils.getUserId()));
    }

    /**
     * 卖家确认预约(待确认→已确认)
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:appointment:ship')")
    @Log(title = "化妆商品预约", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm")
    public AjaxResult confirm(@RequestBody CosmeticsAppointment appointment) {
        if (appointment.getId() == null) {
            return error("预约ID不能为空");
        }
        return toAjax(cosmeticsAppointmentService.confirmAppointment(appointment.getId(), SecurityUtils.getUserId()));
    }

    /**
     * 卖家发货(已确认→已发货), 并订阅物流
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:appointment:ship')")
    @Log(title = "化妆商品预约", businessType = BusinessType.UPDATE)
    @PutMapping("/ship")
    public AjaxResult ship(@RequestBody CosmeticsAppointment appointment) {
        int rows = cosmeticsAppointmentService.shipAppointment(appointment);
        if (rows > 0) {
            logisticsService.subscribe(appointment);
        }
        return toAjax(rows);
    }

    /**
     * 查询物流轨迹
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:appointment:list')")
    @GetMapping("/express/track")
    public AjaxResult track(CosmeticsAppointment appointment) {
        if (appointment.getId() == null) {
            return error("预约ID不能为空");
        }
        CosmeticsAppointment db = cosmeticsAppointmentService.selectVisibleAppointmentById(appointment.getId(),
                SecurityUtils.getUserId());
        if (db == null) {
            return error("预约单不存在");
        }
        return success(logisticsService.queryTrack(db));
    }

    /**
     * 删除预约(软删除, 管理员)
     */
    @PreAuthorize("@ss.hasPermi('cosmetics:appointment:remove')")
    @Log(title = "化妆商品预约", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(cosmeticsAppointmentService.deleteCosmeticsAppointmentByIds(ids));
    }
}
