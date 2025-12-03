# 总体目标
- 全面修复后端与前端缺陷，确保逻辑正确、性能稳定、安全合规、数据一致、API契约不变。
- 引入完整事务与异常处理、统一日志与安全策略、完善测试与回归验证。
- 保持现有页面布局与样式不变，前端交互与状态管理稳定可靠。

## 范围与现状要点
- 后端：Spring Boot 3、Security、JWT、MyBatis/MyBatis-Plus、Redis、MySQL；全局异常仅覆盖 ServiceException；事务边界缺失；配置与安全项有风险；部分 SQL 使用明文密码。
- 前端：Vue 3 + Vite + Element Plus + Axios；已集成 Pinia 但未使用；HTTP拦截器分散；表单验证集中在登录页；无前端测试。
- 关键安全/一致性问题示例：
  - 明文数据库密码与简单 `jwt.secret`；CORS 全放开；CSRF Token 可读；`SameSite=Strict` 与跨域行为可能冲突。
  - 登录 SQL 明文密码比对与 MyBatis 参数命名风险；事务与并发一致性不足。
  - access token 缺少黑名单；缺少服务端登出；前端认证态未集中管理。

# 后端修复计划

## 事务与数据一致性
- 在服务层建立明确事务边界：读写分离、只读事务、传播行为设置。
- 重点改造：用户登录流程、失败计数与锁定逻辑、令牌刷新与黑名单更新、涉及多资源（DB+Redis）操作的组合事务。
- 引入乐观锁或版本字段（必要表）以防止并发写冲突；保证幂等操作（刷新、登出）。

## 错误处理与统一响应
- 扩展 `GlobalExceptionHandler` 覆盖：`MethodArgumentNotValidException/BindException/HttpMessageNotReadableException/AccessDeniedException/AuthenticationException/IllegalArgumentException/RuntimeException`，统一返回 `JsonResult+StatusCode`，记录上下文与请求ID。
- 在控制器入参添加 `@Validated/@Valid` 与精确约束注解，减少非法输入导致的 500。

## 安全与认证改进
- JWT 改造：
  - 加强密钥管理（高熵、外部化、不可明文存储），访问与刷新令牌的独立生命周期与范围；校正刷新窗口与过期计算（`JwtService.java:22` 所示 `refreshExpDays` 的使用与单位转换）。
  - 增加 access token 黑名单；实现服务端 `/auth/logout` 将 refresh 与 access 的 `jti` 写入黑名单。
- Cookie 与 CORS/CSRF：
  - 生产环境收紧 CORS 白名单；`SameSite=Lax` 与 HTTPS `Secure` 配置对齐；评估 `CookieCsrfTokenRepository.withHttpOnlyFalse()` 的必要性，优先使用 HttpOnly。
- 角色/权限：从 JWT Claims 或数据库动态加载用户权限，避免硬编码 `ROLE_ADMIN`。

## 密码存储与认证流程
- 将明文密码替换为 `bcrypt`（或 `Argon2`），并提供一次性迁移脚本，对现有数据库密码进行批量加密。
- 修正 `AdminMapper` 的参数绑定，使用 `@Param`，或统一采用 MyBatis-Plus 的 `BaseMapper` 与条件构造器，避免字符串拼接与命名风险。

## 日志与审计
- 引入标准化日志格式（JSON 或带请求ID/MDC），分类记录：安全事件、认证失败、数据访问异常、慢查询。
- 增加 `logback-spring.xml`，设置级别与异步输出，避免日志 IO 成为瓶颈；屏蔽敏感信息。

## API 边界与契约保障
- 保持 `JsonResult`/`StatusCode` 契约不变；完善边界条件：空入参、重复刷新、过期/黑名单命中、验证码失效、速率超限。
- 统一错误码与文案，补充接口文档（错误码矩阵、示例）。

## 配置与密钥管理
- 将数据库密码、JWT 密钥、Redis 密码等迁移到环境变量或外部配置；移除仓库明文敏感信息。
- 分环境配置（dev/stage/prod），不同 CORS 与安全开关。

# 前端修复计划

## 交互与状态管理
- 引入 Pinia store 管理用户信息与认证态（access/refresh token 来自 Cookie，仅存用户可展示信息），路由守卫基于本地态+必要远端校验，减少不必要请求。
- 统一 HTTP 服务层：所有页面通过 `services/auth.js` 与 `services/http.js` 调用，移除分散 axios 直接使用，确保拦截器一致。

## 表单验证与输入处理
- 复核所有表单（当前以登录为主）：规则与错误提示统一，边界处理（空值、超长、特殊字符）；对输入进行前端层面基本净化与限制；防重提交。

## 异步加载与渲染
- 统一错误重试与刷新逻辑（401 时一次刷新后重试，失败则跳登录）；全局 Loading/错误提示策略一致。
- 集中 BASE_URL 与跨域设置，避免多处定义产生不一致。

## 兼容性与无障碍
- 保持现有布局样式不变；针对 Chromium/Firefox/WebKit 做兼容性验证；键盘可访问性与焦点管理在登录流程中完善。

# 测试与质量保证

## 后端测试
- 单元测试：`JwtService` 编解码、过期与刷新边界；`TokenBlacklistService` 黑名单命中；`AdminService` 登录与失败计数（含并发）。
- 集成测试：基于 Testcontainers 启动 MySQL/Redis，验证事务一致性与安全控制；接口契约测试（`/admin/login`、`/auth/refresh`、`/auth/check`、`/auth/logout`）。
- 性能与稳定性：JMH 或 Gatling 进行重点接口压测（登录、刷新）；慢查询日志与分页校验。

## 前端测试
- 单元测试：使用 Vitest 对 `services/http.js`、`services/auth.js`、路由守卫与表单校验进行测试。
- 端到端测试：使用 Playwright 对登录、刷新、跳转与错误提示进行跨浏览器验证。

## 回归与契约
- 维护完整回归测试套件并纳入 CI；对关键接口保持请求/响应模型不变，新增字段仅向后兼容。

# 交付与文档
- 缺陷修复清单：按模块列出变更与原因、风险与回滚方案。
- 测试报告：覆盖率、通过率、性能指标、跨浏览器验证结果。
- 更新代码：分支与提交说明，不含敏感配置；提供示例环境变量文件。
- 部署说明：环境依赖、密钥管理、分环境配置、迁移步骤（密码加密）、回滚策略。

# 实施步骤（迭代）
1) 建立统一异常与日志框架；完善验证与错误码；不改契约。
2) 引入事务与并发一致性改造（服务层、组合操作）；补齐登出/黑名单。
3) 安全加固（密钥与配置外部化、CORS/CSRF/Cookie 策略、角色权限动态化）。
4) 密码哈希迁移与 Mapper 规范化；必要索引与慢查询治理。
5) 前端统一 HTTP 与状态管理、表单与异步流程校正；保持样式不变。
6) 全面测试与回归、性能与兼容性验证；文档与交付产物汇总。

# 说明与兼容性承诺
- 所有改动以不破坏现有 API 契约为前提；对安全与一致性提升进行内部实现调整。
- 登录与认证流程将更健壮，错误处理一致化；前端用户体验不变但稳定性更强。

请确认以上方案，确认后我将按步骤开始实施与验证，并输出修复清单与测试报告。