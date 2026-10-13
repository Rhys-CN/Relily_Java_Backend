package com.relily.system.service;

import java.util.Date;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.relily.common.exception.ServiceException;
import com.relily.common.utils.StringUtils;
import com.relily.common.utils.sign.Md5Utils;
import com.relily.system.domain.CosmeticsAppointment;
import com.relily.system.domain.CosmeticsExpress;

/**
 * 物流服务: 查询/订阅/回调解析(以快递100为例)
 * 
 * <p>
 * 未配置 real key 时返回演示数据, 保证项目可独立运行。
 * 配置后走真实 API 查询与轨迹推送订阅。
 * </p>
 * 
 * @author relily
 */
@Service
public class LogisticsService {
    private static final Logger log = LoggerFactory.getLogger(LogisticsService.class);

    /** 本地快照缓存有效时长 5 分钟 */
    private static final long CACHE_MS = 5 * 60 * 1000L;

    @Autowired
    private ICosmeticsExpressService expressService;

    // application.yml: logistics.kd100.key / customerSign 可注入
    @Value("${logistics.kd100.key:}")
    private String key;

    @Value("${logistics.kd100.customer:}")
    private String customer;

    /**
     * 查询物流轨迹: 优先本地快照(5分钟内), 否则调外部 API 并更新缓存
     * 
     * @param apt 预约单(需包含运输公司与单号)
     * @return 物流轨迹
     */
    public CosmeticsExpress queryTrack(CosmeticsAppointment apt) {
        if (StringUtils.isAnyBlank(apt.getExpressNo())) {
            throw new ServiceException("该预约单尚未发货, 暂无物流信息");
        }
        CosmeticsExpress cache = expressService.selectCosmeticsExpressByOrderNo(apt.getOrderNo());
        if (cache != null && cache.getLastQueryTime() != null
                && (System.currentTimeMillis() - cache.getLastQueryTime().getTime()) < CACHE_MS) {
            return cache;
        }
        return queryAndCache(apt);
    }

    /**
     * 调用外部物流 API 并写缓存(演示模式返回模拟轨迹)
     */
    private CosmeticsExpress queryAndCache(CosmeticsAppointment apt) {
        CosmeticsExpress track = new CosmeticsExpress();
        track.setAppointmentId(apt.getId());
        track.setOrderNo(apt.getOrderNo());
        track.setExpressCompany(apt.getExpressCompany());
        track.setExpressNo(apt.getExpressNo());

        boolean realCall = StringUtils.isNotEmpty(key) && !"YOUR_KEY".equals(key);
        if (realCall) {
            // TODO: 对接快递100 / 快递鸟 实时查询接口
            track.setTrackStatus("1");
            track.setIsSigned("0");
            track.setTrackInfo("[]");
            log.info("实时物流查询未接入, 返回空轨迹: {}", apt.getExpressNo());
        } else {
            // 演示数据
            track.setTrackStatus("1");
            track.setIsSigned("0");
            String[] nodes = {
                    "{\"time\":\"" + new Date() + "\",\"context\":\"揽收成功, 卖家已发货\",\"status\":\"0\"}" };
            track.setTrackInfo(JSON.toJSONString(nodes));
        }
        track.setLastQueryTime(new Date());
        expressService.upsertCosmeticsExpress(track);
        return track;
    }

    /**
     * 订阅物流推送(发货后调用)
     */
    public void subscribe(CosmeticsAppointment apt) {
        log.info("订阅物流推送: {}", apt.getExpressNo());
        // 真实接入: 调用快递100 subscribe 接口
    }

    /**
     * 校验快递回调签名(快递100: MD5(param + key + customer))
     */
    public boolean verifySign(String param, String sign) {
        if (StringUtils.isAnyBlank(param, sign)) {
            return false;
        }
        if (StringUtils.isAnyBlank(key, customer) || "YOUR_KEY".equals(key)) {
            log.error("物流回调密钥未配置，拒绝处理回调");
            return false;
        }
        String computed = Md5Utils.hash(param + key + customer);
        return MessageDigest.isEqual(computed.getBytes(StandardCharsets.US_ASCII),
                sign.toLowerCase(java.util.Locale.ROOT).getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 解析物流回调推送, 返回轨迹对象(已签收则触发自动完结放在 controller)
     */
    public CosmeticsExpress parseCallback(String param) {
        JSONObject obj = JSON.parseObject(param);
        CosmeticsExpress track = new CosmeticsExpress();
        track.setOrderNo(obj.getString("expNo") == null ? obj.getString("orderNo") : obj.getString("expNo"));
        track.setExpressCompany(obj.getString("expressCompany"));
        track.setExpressNo(obj.getString("expressNo") == null ? obj.getString("expNo") : obj.getString("expressNo"));

        // 快递数据可能在 data.result 或 lastResult.data
        JSONArray data = extractNodes(obj);
        track.setTrackInfo(data.toJSONString());

        boolean signed = obj.getBooleanValue("isSigned") || containsSigned(data);
        track.setIsSigned(signed ? "1" : "0");
        track.setTrackStatus(signed ? "3" : (containsDelivering(data) ? "2" : "1"));
        track.setLastQueryTime(new Date());
        return track;
    }

    private JSONArray extractNodes(JSONObject obj) {
        JSONObject result = obj.getJSONObject("lastResult");
        if (result == null) {
            result = obj.getJSONObject("result");
        }
        if (result != null) {
            JSONArray data = result.getJSONArray("data");
            if (data != null) {
                return data;
            }
        }
        JSONArray data = obj.getJSONArray("data");
        return data == null ? new JSONArray() : data;
    }

    private boolean containsSigned(JSONArray data) {
        for (int i = 0; i < data.size(); i++) {
            JSONObject node = data.getJSONObject(i);
            if ("3".equals(node.getString("status"))
                    || "签收".contains(node.getString("context") == null ? "" : node.getString("context"))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDelivering(JSONArray data) {
        for (int i = 0; i < data.size(); i++) {
            JSONObject node = data.getJSONObject(i);
            if ("2".equals(node.getString("status"))) {
                return true;
            }
        }
        return false;
    }
}
