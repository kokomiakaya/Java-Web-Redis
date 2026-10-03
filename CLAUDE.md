# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目定位

黑马程序员（itheima）课程项目：tlias 员工管理系统后端，Spring Boot REST API 服务（无前端页面，`static/upload.html` 仅为上传测试页）。面向访客的完整介绍见 [README.md](README.md)。

## 构建与运行

- 环境要求：JDK 21（pom.xml 中 `java.version=21`）。Maven 使用项目自带 wrapper，无需全局安装。
- 启动：Windows `mvnw.cmd spring-boot:run`；Unix `./mvnw spring-boot:run`（首次运行 wrapper 会自动下载 Maven）。
- 测试：`mvnw.cmd test`（测试在 `src/test/java/com/itheima/`，目前仅冒烟级）。
- 依赖数据库：MySQL `localhost:3306/tlias`，表 `dept`/`emp`/`emp_expr`/`emp_log`/`clazz`/`student`。**仓库不含建表 SQL**，字段以 `pojo/` 下的实体类为准。
- 服务端口：8080（Spring Boot 默认，未配置 server.port）。
- 运行前提（文件上传）：需先设置环境变量 `ALIBABA_CLOUD_ACCESS_KEY_ID` / `ALIBABA_CLOUD_ACCESS_KEY_SECRET`（阿里云 OSS 凭据，`AliyunOSSOperator` 用 `EnvironmentVariableCredentialsProvider` 读取），否则 `/upload` 抛异常。**密钥严禁写入代码/配置文件，只能走环境变量。**

## 架构与代码组织

分层为 Controller → Service 接口 → ServiceImpl → Mapper（接口），Mapper XML 位于 `src/main/resources/com/itheima/mapper/`。新增模块按此模板复制。

- `controller/`：DeptController（/depts）、EmpController（/emps，含查询回显与修改）、LoginController（/login，员工登录）、ClazzController（/clazzs，班级分页查询 + 增删改查）、StudentController（/students，学员 CRUD + 违纪扣分）、ReportController（/report，员工/学员报表统计）、UploadController（/upload，阿里云 OSS）、SessionController（/c1 /c2 /s1 /s2，Cookie/Session 教学演示）。控制器只做参数接收与日志，业务全在 Service。
- `utils/`：AliyunOSSOperator——OSS 上传封装，endpoint/bucket/region 硬编码在类字段中（换账号需改）；JwtUtils——JWT 签发/解析（signKey 硬编码，见陷阱）。
- `config/`：WebConfig——拦截器注册（当前仅注册 DemoInterceptor，拦截 `/**` 排除 /login）。
- `filter/`：DemoFilter（教学演示，@WebFilter 注释未启用）、TokenFilter（令牌校验：url 含 login 放行，token 空/解析失败回 401；@WebFilter 注释未启用）。
- `interceptor/`：DemoInterceptor（教学演示，pre/post/after 打日志）、TokenInterceptor（令牌校验 + 额外要求 username 请求参数非空否则回 400；@Component 但未注册进 WebConfig）。启动类已开 @ServletComponentScan。
- `exception/`：GlobalExceptionHandler——`@RestControllerAdvice` catch-all，统一返回 `Result.error(e.getMessage())`（消息透传）；BusinessException——自定义业务异常（班级删除保护用）。
- `pojo/`：实体（Dept/Emp/EmpExpr/EmpLog/Clazz/Student）、查询参数 EmpQueryParam、报表封装 JobOption / ClazzCountOption（jobList/dataList、clazzList/dataList，原始 List 类型，供 ECharts）、登录返回 LoginInfo（id/username/name/token，token 恒为 null）、分页封装 PageResult、统一响应 Result。Clazz 含联查字段 masterName（班主任姓名）与 status（SQL CASE 计算的开班状态）；Student 含联查字段 clazzName。
- 课程教学代码：controller/service 中保留大量注释掉的旧版迭代，属正常现象，勿删除。

## 关键约定与模式（修改代码前必读）

1. **统一响应**：所有接口返回 `Result{code, msg, data}`，`Result.success()` 表示 code=1 成功。新接口必须遵守，不要直接返回裸对象。
2. **分页**：`PageHelper.startPage(page, pageSize)` 必须紧跟查询语句（见 `EmpServiceImpl.page`），查询结果强转 `Page<Emp>` 后取 `getTotal()/getResult()` 封装 `PageResult`。
3. **动态 SQL**：员工筛选条件写在 `EmpMapper.xml` 的 `<where>`+`<if>` 中，新增筛选条件需同时改 `EmpQueryParam`、`EmpMapper.xml`。
4. **事务边界**：写操作 `@Transactional` 标注在 ServiceImpl 方法上（`save` 用 `rollbackFor = Exception.class`）。
5. **审计日志（不可破坏的模式）**：`EmpServiceImpl.save` 在 `finally` 中调用 `empLogService.insertLog`，`EmpLogServiceImpl` 用 `Propagation.REQUIRES_NEW` 独立事务写 `emp_log`——即使业务回滚，审计记录也会提交。修改业务事务逻辑时不要改动这一层。
6. **批量删除**：`deleteByIds` 同时删除 `emp` 与关联的 `emp_expr`，新增关联数据时同步维护。
7. **查询回显（一对多映射）**：`EmpMapper.getById` 用 `resultMap`（`empResultMap`）+ `<collection>` 映射 emp_expr（SQL 别名 `ee_` 前缀），返回 `Emp.exprList`。新增「实体 + 集合」查询按此模式。
8. **员工修改（三步流程，注意）**：`EmpServiceImpl.update` 依次执行：更新基本信息（`updateById` 动态 `<set>`）→ 按 empId 删除旧工作经历 → 批量插入新经历。**该方法未加 `@Transactional`，三步非原子**；教学代码保持现状，如参照 `save` 补事务需先与课程进度确认。
9. **OSS 上传**：凭据只允许通过环境变量读取（`EnvironmentVariableCredentialsProvider`），不要把密钥写进代码或配置文件；OSS 客户端用 try-with-resources 创建。
10. **全局异常**：GlobalExceptionHandler 拦截所有异常，统一返回 `Result.error("对不起，操作失败，请联系管理员")`，堆栈仅 `printStackTrace`。
11. **班级分页查询**：沿用 PageHelper 模板（参照 `ClazzServiceImpl.page`）；`ClazzMapper.list` 用 SQL `CASE` 计算 status（未开班/在读中/已结课）并 left join emp 取班主任姓名——状态列在 SQL 计算而非 Java 计算。
12. **报表聚合**：`EmpMapper` 的 countEmpJobData/countEmpGenderData 用 `group by` + `CASE` 转中文标签，返回 `List<Map>`/`JobOption` 供 ECharts 饼图；`@MapKey` 标注在返回 List 的方法上无效（课程遗留，勿效仿）。
13. **删除保护**：删除班级前先 `studentMapper.countByClazzId(id)` 校验，有学员则抛 `BusinessException`，由全局异常处理器把消息透传给前端——新增「有关联数据的删除」按此模式。
14. **违纪扣分**：用单条 UPDATE 原子累加（`violation_count = violation_count + 1, violation_score = violation_score + #{score}`），勿用「读-改-写」。
15. **登录与 JWT**：`EmpServiceImpl.login` 校验成功后调用 `JwtUtils.generateJwt` 签发 JWT（HS256、claims 含 id/username、有效期 12 小时），返回含 token 的 LoginInfo；解析用 `JwtUtils.parseJwt`。**注意：TokenFilter/TokenInterceptor 均未启用**（@WebFilter 被注释、WebConfig 只注册了 DemoInterceptor）——接口实际仍无鉴权，不要假设 token 已被校验。
16. **过滤器 vs 拦截器**：课程给了两套令牌校验实现——TokenFilter（Filter 接口）与 TokenInterceptor（HandlerInterceptor，额外校验 username 参数）。启用方式：Filter 取消 @WebFilter 注释（依赖 @ServletComponentScan，已开）；Interceptor 注册进 WebConfig.addInterceptors。

## 本机环境陷阱（换机器先改这些）

以下为本机绝对路径/开发凭据，换环境运行前必须修改：

1. `UploadController.UPLOAD_DIR`（`E:\CodeJava\web-ai-project02\images\`）**已废弃的死代码**——本地存盘上传已被 OSS 替代，旧实现仅注释保留，勿恢复
2. `logback.xml` 文件日志写死 `E:/CodeHTML-CSS/log/`
3. `application.yml` 数据库密码 `root/123456`（本地开发默认值，勿用于生产）
4. `AliyunOSSOperator` 的 endpoint/bucketName/region 三个硬编码字段（换账号/Bucket 必须改）
5. `src/test/java/com/itheima/Demo.java` 写死本机文件 `E:\CodeJava\web-ai-project02\images\0f7b82fb...jpg`（仅演示类）
6. 两个 OSS 环境变量 `ALIBABA_CLOUD_ACCESS_KEY_ID` / `ALIBABA_CLOUD_ACCESS_KEY_SECRET` 必须先设置
7. `JwtUtils.signKey` 硬编码 `SVRIRUlNQQ==`（base64("itheima")）——教学用，生产必须改外部配置；旧版 jjwt 0.9.1 在 JDK 21 依赖 jaxb-api（pom.xml 已加，勿删）

## 现状与缺口

- 无登录鉴权/权限控制：`/login` 已签发 JWT，但令牌校验未启用（TokenFilter 的 @WebFilter 被注释、TokenInterceptor 未注册进 WebConfig）——接口仍全部开放。
- 全局异常处理器为 catch-all：透传 `e.getMessage()`（无消息的异常如 NPE 会返回 null msg），堆栈仅 printStackTrace，无分类型错误码。
- `EmpServiceImpl.update` 未加 `@Transactional`（三步操作非原子）。
- OSS 配置硬编码在 `AliyunOSSOperator` 类字段中，未提取到配置文件。
- `ClazzMapper.xml` 的 `update` 模板已在 v0.4 接线（绑定 `ClazzMapper.update`）。
- `StudentMapper.xml` 的 insertBatch 无接口方法调用（孤儿语句）。
- Report 接口返回原始 `List<Map>`/`JobOption`/`ClazzCountOption` 原始类型，无类型安全。
- 多处 unused import（课程模板遗留，勿效仿）：StudentController 的 lombok.Data、StudentMapper 的 Service、ClazzMapper 的 DeleteMapping 等。
- 无 Redis、无 AI 相关代码——**仓库名 Java-Web-Redis 与实际内容不符，勿据此添加无关依赖**。
- 测试薄弱：仅冒烟级 JUnit 类，`Demo.java`/`Example.java` 为 main 方法演示类（非测试）；改完代码需手动验证。

## 手动验证

无前端项目时，改完接口用 curl 或 `src/main/resources/static/upload.html` 页面手测。示例：`curl http://localhost:8080/depts`；上传：`curl -F "file=@xxx.jpg" http://localhost:8080/upload`（需已设置 OSS 环境变量）。
