# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目定位

黑马程序员（itheima）课程项目：tlias 员工管理系统后端，Spring Boot REST API 服务（无前端页面，`static/upload.html` 仅为上传测试页）。面向访客的完整介绍见 [README.md](README.md)。

## 构建与运行

- 环境要求：JDK 21（pom.xml 中 `java.version=21`）。Maven 使用项目自带 wrapper，无需全局安装。
- 启动：Windows `mvnw.cmd spring-boot:run`；Unix `./mvnw spring-boot:run`（首次运行 wrapper 会自动下载 Maven）。
- 测试：`mvnw.cmd test`（测试在 `src/test/java/com/itheima/`，目前仅冒烟级）。
- 依赖数据库：MySQL `localhost:3306/tlias`，表 `dept`/`emp`/`emp_expr`/`emp_log`。**仓库不含建表 SQL**，字段以 `pojo/` 下的实体类为准。
- 服务端口：8080（Spring Boot 默认，未配置 server.port）。

## 架构与代码组织

分层为 Controller → Service 接口 → ServiceImpl → Mapper（接口），Mapper XML 位于 `src/main/resources/com/itheima/mapper/`。新增模块按此模板复制。

- `controller/`：DeptController（/depts）、EmpController（/emps）、UploadController（/upload）。控制器只做参数接收与日志，业务全在 Service。
- `pojo/`：实体（Dept/Emp/EmpExpr/EmpLog）、查询参数 EmpQueryParam、分页封装 PageResult、统一响应 Result。
- 课程教学代码：controller/service 中保留大量注释掉的旧版迭代，属正常现象，勿删除。

## 关键约定与模式（修改代码前必读）

1. **统一响应**：所有接口返回 `Result{code, msg, data}`，`Result.success()` 表示 code=1 成功。新接口必须遵守，不要直接返回裸对象。
2. **分页**：`PageHelper.startPage(page, pageSize)` 必须紧跟查询语句（见 `EmpServiceImpl.page`），查询结果强转 `Page<Emp>` 后取 `getTotal()/getResult()` 封装 `PageResult`。
3. **动态 SQL**：员工筛选条件写在 `EmpMapper.xml` 的 `<where>`+`<if>` 中，新增筛选条件需同时改 `EmpQueryParam`、`EmpMapper.xml`。
4. **事务边界**：写操作 `@Transactional` 标注在 ServiceImpl 方法上（`save` 用 `rollbackFor = Exception.class`）。
5. **审计日志（不可破坏的模式）**：`EmpServiceImpl.save` 在 `finally` 中调用 `empLogService.insertLog`，`EmpLogServiceImpl` 用 `Propagation.REQUIRES_NEW` 独立事务写 `emp_log`——即使业务回滚，审计记录也会提交。修改业务事务逻辑时不要改动这一层。
6. **批量删除**：`deleteByIds` 同时删除 `emp` 与关联的 `emp_expr`，新增关联数据时同步维护。

## 本机环境陷阱（换机器先改三处）

以下为本机绝对路径/开发凭据，换环境运行前必须修改：

1. `UploadController.UPLOAD_DIR` 写死 `E:\CodeJava\web-ai-project02\images\`（上传保存目录）
2. `logback.xml` 文件日志写死 `E:/CodeHTML-CSS/log/`
3. `application.yml` 数据库密码 `root/123456`（本地开发默认值，勿用于生产）

## 现状与缺口

- 无登录鉴权/权限控制，无全局异常处理器（错误直接抛给容器）。
- 无 Redis、无 AI 相关代码——**仓库名 Java-Web-Redis 与实际内容不符，勿据此添加无关依赖**。
- 测试仅两个冒烟类，无自动化接口测试；改完代码需手动验证。

## 手动验证

无前端项目时，改完接口用 curl 或 `src/main/resources/static/upload.html` 页面手测。示例：`curl http://localhost:8080/depts`。
