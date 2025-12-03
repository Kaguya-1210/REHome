# 缺陷修复清单

## 后端
- 全局异常处理扩展，统一 `JsonResult/StatusCode` 响应。
- 新增请求ID拦截器，日志关联请求。
- 新增 `logback-spring.xml`，统一日志格式与级别。
- 新增 `/auth/logout`，黑名单处理 access/refresh 并清理 Cookie。
- 登录服务增加只读事务边界。
- 外部化敏感配置（数据库、Redis、JWT 密钥），新增可配置 CORS 白名单。
- 登录认证改造：Mapper 改为按账号查询；接入 BCrypt 校验；首次明文密码登录自动迁移为哈希。

## 前端
- 登录页改用统一 `services/auth` 与 `services/http`，移除分散 axios。
- 新增 Pinia 用户态，路由守卫优先本地态再远端校验。
- 保持页面布局与样式不变。

## 安全与一致性
- 保留现有 CSRF 行为，支持生产按需收紧；令牌黑名单覆盖登出与刷新场景。

