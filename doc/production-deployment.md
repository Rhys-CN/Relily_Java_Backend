# 生产部署基线

## 必填环境变量

应用启动前必须由部署平台、密钥管理服务或受权限控制的外部配置文件注入以下变量。不要将真实值提交到 Git、镜像或 CI 日志。

```text
DB_URL=jdbc:mysql://db.internal:3306/learnproject?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
DB_USERNAME=application_user
DB_PASSWORD=<strong-random-password>
REDIS_HOST=redis.internal
REDIS_PORT=6379
REDIS_PASSWORD=<strong-random-password>
JWT_SECRET=<at-least-32-random-bytes>
```

只有在受限管理网络中排障时才启用 Druid：

```text
DRUID_STAT_VIEW_ENABLED=true
DRUID_WEB_STAT_FILTER_ENABLED=true
DRUID_ALLOW=10.0.0.10,10.0.0.11
DRUID_USERNAME=<admin-username>
DRUID_PASSWORD=<strong-random-password>
```

物流回调必须配置供应商提供的凭据，否则回调会被拒绝：

```text
LOGISTICS_KD100_KEY=<provider-key>
LOGISTICS_KD100_CUSTOMER=<provider-customer-id>
```

Spring 的环境变量会把 `LOGISTICS_KD100_KEY` 映射为 `logistics.kd100.key`，把 `LOGISTICS_KD100_CUSTOMER` 映射为 `logistics.kd100.customer`。

## 生产开关

- `SPRING_PROFILES_ACTIVE=prod,druid`
- `SPRING_DEVTOOLS_RESTART_ENABLED=false`
- `SPRINGDOC_SWAGGER_UI_ENABLED=false`
- `DRUID_STAT_VIEW_ENABLED=false`
- `DRUID_WEB_STAT_FILTER_ENABLED=false`
- 前端通过反向代理将 `/prod-api/` 转发给后端；不要在生产构建中配置开发代理地址。

若确需启用 Swagger，仅限内网网关或 VPN 访问，并由网关实施访问控制。应用本身不再将 Swagger 或 Druid 配置为匿名访问。

## 上线前检查

1. 轮换文档中曾出现过的数据库、Redis、Druid 和 JWT 凭据；仅删除配置文件中的明文不足以消除泄露风险。
2. 使用最小权限数据库账户，禁止应用账户执行 DDL 或访问无关 schema。
3. 配置 HTTPS、HSTS、请求体大小限制、反向代理速率限制和集中式访问日志脱敏。
4. 执行 `mvn verify`、`npm ci`、`npm run build:prod` 与依赖漏洞扫描。
5. 在预发布环境验证下单、并发取消、超时取消、越权访问和物流验签失败等关键流程。
