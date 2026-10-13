package com.relily.web.controller.system;

import com.relily.common.annotation.Anonymous;
import com.relily.system.domain.CosmeticsExpress;
import com.relily.system.service.ICosmeticsAppointmentService;
import com.relily.system.service.ICosmeticsExpressService;
import com.relily.system.service.LogisticsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.relily.common.core.controller.BaseController;
import com.relily.common.core.domain.AjaxResult;

/**
 * 物流回调接口(快递100/快递鸟推送)
 *
 * <p>
 * 回调地址需公网可访问, 且必须验签, 防止伪造物流状态。
 * </p>
 * 
 * @author relily
 */
@RestController
@RequestMapping("/system/cosmeticsExpress")
public class CosmeticsExpressController extends BaseController {
    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private ICosmeticsExpressService expressService;

    @Autowired
    private ICosmeticsAppointmentService appointmentService;

    /**
     * 物流推送回调: 验签 → 更新轨迹 → 签收自动完结(4→3)
     * 
     * @param request 请求(需携带 param / sign 参数)
     * @return "ok"/"fail"
     */
    @Anonymous
    @PostMapping("/express/callback")
    public String callback(HttpServletRequest request) {
        String param = request.getParameter("param");
        String sign = request.getParameter("sign");

        // 1. 强制验签, 防止伪造物流状态
        if (!logisticsService.verifySign(param, sign)) {
            return "fail";
        }

        // 2. 解析并更新本地轨迹快照
        CosmeticsExpress track = logisticsService.parseCallback(param);
        expressService.upsertCosmeticsExpress(track);

        // 3. 签收自动完结: 4(已发货) → 3(已完成)
        if ("1".equals(track.getIsSigned())) {
            appointmentService.autoComplete(track.getOrderNo());
        }
        return "ok";
    }

    /**
     * 模拟推送入口(便于联调物流闭环, 正式环境可移除)
     */
    @PreAuthorize("@ss.hasRole('admin')")
    @PostMapping("/express/callback/demo")
    public AjaxResult callbackDemo(String orderNo, boolean signed) {
        if (orderNo == null) {
            return error("orderNo 不能为空");
        }
        String param = "{\"orderNo\":\"" + orderNo + "\",\"isSigned\":" + signed + "}";
        CosmeticsExpress track = logisticsService.parseCallback(param);
        expressService.upsertCosmeticsExpress(track);
        if (signed) {
            appointmentService.autoComplete(orderNo);
        }
        return success(track);
    }
}
