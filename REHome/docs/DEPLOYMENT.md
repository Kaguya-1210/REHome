# 部署说明

## 环境变量
- `DB_PASSWORD`：数据库密码
- `REDIS_PASSWORD`：Redis 密码
- `JWT_SECRET`：JWT 密钥（高熵随机）
- `REQUIRE_HTTPS`：是否强制 HTTPS（prod 建议 `true`）
- `CORS_ALLOWED_ORIGINS`：CORS 白名单（逗号分隔），对应 `cors.allowed-origins`

## 步骤
1. 配置环境变量与外部化配置文件。
2. 初始化数据库索引与账号数据；迁移明文密码为 BCrypt（首登自动迁移或批量脚本）。
3. 启动 Redis 与 MySQL；验证连接与健康检查。
4. 启动后端服务与前端应用；校验登录、刷新、登出与路由守卫。
5. 观察日志与黑名单行为；执行回归测试。

## 回滚策略
- 采用按步骤回滚：关闭新功能开关（CORS 收紧等）、恢复旧配置；保留日志与黑名单键以便审计。

