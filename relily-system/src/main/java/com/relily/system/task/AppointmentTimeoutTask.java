package com.relily.system.task;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.relily.system.service.ICosmeticsAppointmentService;

/**
 * 预约超时自动取消定时任务
 * 
 * <p>
 * cron 每5分钟执行一次。通过 sys_job 表调度(demo 里已内置一行), invoke_target:
 * appointmentTimeoutTask.cancelExpired
 * </p>
 * 
 * @author relily
 */
@Component("appointmentTimeoutTask")
public class AppointmentTimeoutTask {
    private static final Logger log = LoggerFactory.getLogger(AppointmentTimeoutTask.class);

    @Autowired
    private ICosmeticsAppointmentService cosmeticsAppointmentService;

    /**
     * 扫描待确认超时的预约并自动取消、回补库存(幂等)
     */
    public void cancelExpired() {
        log.info("预约超时扫描开始...");
        Date now = new Date();
        int cancelled = cosmeticsAppointmentService.cancelExpiredAppointments(now);
        log.info("预约超时扫描结束, 成功取消 {} 单", cancelled);
    }
}
