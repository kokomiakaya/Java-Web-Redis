# tlias-web-management 员工管理系统（后端）

黑马程序员（itheima）JavaWeb 课程项目 —— tlias 智能学习辅助系统中**员工管理模块的后端服务**，纯 REST API 项目，无前端页面（仓库内 `upload.html` 仅用于文件上传接口的手动测试）。

> 当前版本：**v1.0（后端完成版）**（更新日志见文末）

> ⚠️ 说明：仓库名为 Java-Web-Redis，但本项目实际是员工管理系统，**当前代码中不包含 Redis 或 AI 相关功能**。

## 技术栈

| 技术 | 版本/说明 |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.7（spring-boot-starter-webmvc） |
| MyBatis | mybatis-spring-boot-starter 4.0.1（XML 动态 SQL） |
| MySQL | mysql-connector-j |
| 分页 | PageHelper 1.4.7 |
| 阿里云 OSS | alibabacloud-oss-v2 0.6.0（文件上传存储） |
| JWT | jjwt 0.9.1 + jaxb-api 2.3.1（登录令牌签发/解析） |
| Spring AOP | spring-boot-starter-aspectj（操作日志、耗时统计切面） |
| Actuator | spring-boot-starter-actuator（应用监控端点） |
| 其他 | Lombok、SLF4J + Logback |
| 构建 | Maven（自带 wrapper，无需全局安装） |

## 功能列表

- **部门管理**：查询全部、按 ID 查询、新增、修改、删除
- **员工管理**：分页查询（默认第 1 页 / 每页 10 条）、按姓名/性别/入职日期区间筛选、新增员工（含工作经历批量保存）、按 ID 查询详情回显（含工作经历）、修改员工（基本信息 + 工作经历重写）、批量删除（连带删除工作经历）
- **班级管理**：条件分页查询（按名称模糊 / 开班日期区间筛选，返回班主任姓名与开班状态）、全部查询、新增、详情、修改、删除（删除保护：班下有学员时拒绝删除）
- **学员管理**：分页条件查询（姓名/学历/班级筛选）、详情、新增、修改、批量删除、违纪扣分（违纪次数与扣分自动累计）
- **登录认证**：员工用户名密码校验（`POST /login`），登录成功后签发 JWT 令牌（HS256，有效期 12 小时，携带 id/username）
- **JWT 鉴权（已启用）**：`TokenInterceptor` 拦截除 /login 外的所有请求，校验请求头 token 并解析员工 id 存入 ThreadLocal（`CurrentHolder`）；无效令牌返回 401（`TokenFilter` 过滤器版本保留但未启用）
- **AOP 操作日志**：标注 `@Log` 的接口自动记录操作人/请求参数/返回值/耗时到 `operate_log` 表（`OperationLogAspect`）
- **AOP 耗时统计**：`RecordTimeAspect` 统计 Service 层每个方法的执行耗时
- **报表统计**：员工职位/性别分布、学员学历分布、班级学员人数统计（ECharts 饼图/柱状图数据格式）
- **事务与审计**：员工写操作记入 `emp_log` 审计表；日志写入使用 `REQUIRES_NEW` 独立事务，业务回滚时审计记录仍然保留
- **文件上传（阿里云 OSS）**：multipart 上传至阿里云 OSS，对象名按 `yyyy/MM/uuid.后缀` 组织，上传成功返回文件访问 URL
- **统一响应**：所有接口返回 `{code, msg, data}`，`code=1` 表示成功
- **全局异常处理**：`@RestControllerAdvice` 统一捕获未处理异常并返回 `Result.error`，异常堆栈不再直接暴露给调用方

## API 端点

> 🔐 鉴权说明：除 `POST /login` 外，所有接口需在请求头携带 `token: <JWT>`（登录接口返回），无效令牌返回 401。
>
> 📝 带 `@Log` 标注的接口（POST /clazzs、POST /depts、GET /depts/{id}、POST /students、DELETE /students/{ids}）会自动记录操作日志到 `operate_log` 表。

| 方法 | 路径 | 参数 | 说明 |
|---|---|---|---|
| POST | `/login` | body: Emp JSON（username、password） | 员工登录（校验成功后签发 JWT，返回含 token 的 LoginInfo） |
| GET | `/depts` | - | 查询所有部门 |
| GET | `/depts/{id}` | 路径参数 id | 按 ID 查询部门 |
| POST | `/depts` | body: Dept JSON | 新增部门 |
| PUT | `/depts` | body: Dept JSON | 修改部门 |
| DELETE | `/depts?id=1` | 查询参数 id | 删除部门（注意：id 在查询参数中） |
| GET | `/emps` | page、pageSize、name、gender、begin、end | 分页 + 条件查询员工 |
| GET | `/emps/{id}` | 路径参数 id | 按 ID 查询员工详情（含工作经历，用于修改回显） |
| POST | `/emps` | body: Emp JSON | 新增员工（可含工作经历列表 exprList） |
| PUT | `/emps` | body: Emp JSON（含 exprList） | 修改员工（基本信息 + 工作经历重写） |
| DELETE | `/emps?ids=1,2,3` | 查询参数 ids | 批量删除员工 |
| GET | `/clazzs` | name、begin、end、page、pageSize | 班级条件分页查询（名称模糊、开班日期区间，含班主任姓名与开班状态） |
| GET | `/clazzs/list` | - | 查询全部班级 |
| POST | `/clazzs` | body: Clazz JSON | 新增班级 |
| GET | `/clazzs/{id}` | 路径参数 id | 按 ID 查询班级 |
| PUT | `/clazzs` | body: Clazz JSON | 修改班级 |
| DELETE | `/clazzs/{id}` | 路径参数 id | 删除班级（班下有学员时返回业务异常） |
| GET | `/students` | name、degree、clazzId、page、pageSize | 学员分页 + 条件查询（姓名/学历/班级筛选） |
| GET | `/students/{id}` | 路径参数 id | 按 ID 查询学员详情 |
| POST | `/students` | body: Student JSON | 新增学员 |
| PUT | `/students` | body: Student JSON | 修改学员 |
| DELETE | `/students/{ids}` | 路径参数 ids | 批量删除学员 |
| PUT | `/students/violation/{id}/{score}` | 路径参数 id、score | 违纪扣分（违纪次数 +1、扣分累计） |
| GET | `/report/empJobData` | - | 员工职位分布统计（ECharts 饼图数据） |
| GET | `/report/empGenderData` | - | 员工性别分布统计（ECharts 饼图数据） |
| GET | `/report/studentDegreeData` | - | 学员学历分布统计（ECharts 饼图数据） |
| GET | `/report/studentCountData` | - | 班级学员人数统计（ECharts 柱状图数据） |
| POST | `/upload` | form-data：file | 上传文件至阿里云 OSS，成功返回 `data = 文件访问 URL` |

Cookie/Session 教学演示接口（无业务含义，用于演示请求与会话状态）：

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/c1` | 设置 Cookie（login_username=itheima） |
| GET | `/c2` | 读取 Cookie |
| GET | `/s1` | 创建 Session 并保存 loginUser |
| GET | `/s2` | 读取 Session 中的 loginUser |

响应示例（分页查询）：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 16,
    "rows": [
      { "id": 1, "name": "张三", "gender": 1, "deptName": "学工部", "entryDate": "2022-09-01" }
    ]
  }
}
```

响应示例（文件上传成功）：

```json
{
  "code": 1,
  "msg": "success",
  "data": "https://java-web-buwlc.oss-cn-shanghai.aliyuncs.com/2026/10/xxxxxxxx-xxxx-xxxx-xxxx-xxxx.jpg"
}
```

## 数据库

- 数据库：MySQL，库名 `tlias`（连接信息见 `application.yml`）
- ⚠️ **仓库不包含建表 SQL，需要自行建库建表**，字段以 `src/main/java/com/itheima/pojo/` 下的实体类为准

各表字段参考（以下划线命名对应驼峰，如 `entry_date` ↔ `entryDate`）：

| 表 | 字段 |
|---|---|
| dept | id、name、create_time、update_time |
| emp | id、username、password、name、gender（1男/2女）、phone、job（1班主任/2讲师/3学工主管/4教研主管/5咨询师）、salary、image、entry_date、dept_id、create_time、update_time |
| emp_expr（工作经历） | id、emp_id、begin、end、company、job（职位名） |
| emp_log（操作审计） | id、operate_time、info |
| operate_log（AOP 操作日志） | id、operate_emp_id、operate_time、class_name、method_name、method_params、return_value、cost_time |
| clazz（班级） | id、name、room、begin_date、end_date、master_id、subject、create_time、update_time（查询结果中的 master_name 班主任姓名、status 开班状态为 SQL 计算列） |
| student（学员） | id、name、no、gender、phone、id_card、is_college、address、degree、graduation_date、clazz_id、violation_count、violation_score、create_time、update_time（查询结果中的 clazz_name 班级名称来自联查 clazz 表） |

## 快速开始

### 环境要求

- JDK 21
- MySQL 8.x
- Maven 3.9+（可选——直接用项目自带 wrapper 即可）
- 阿里云 OSS 账号（文件上传功能需要）

### 步骤

1. 创建数据库 `tlias` 并建表（表结构见上文）
2. **设置阿里云 OSS 凭据环境变量**（代码通过 `EnvironmentVariableCredentialsProvider` 读取，不配置时上传接口会报错）：
   - `ALIBABA_CLOUD_ACCESS_KEY_ID`
   - `ALIBABA_CLOUD_ACCESS_KEY_SECRET`
   > 🔒 密钥只存在于环境变量中，**代码与配置文件中不含任何密钥**，仓库上传到 GitHub 无泄露风险
3. 按需修改 `src/main/resources/application.yml` 中的数据库用户名/密码（默认 `root/123456`）
4. 检查/修改硬编码路径与 OSS 配置（见下方配置说明）
5. 启动项目：
   - Windows：`mvnw.cmd spring-boot:run`
   - Linux/Mac：`./mvnw spring-boot:run`
   - 或在 IDEA 中直接运行 `TliasWebManagementApplication`
6. 验证：
   - 先登录获取令牌：`curl -X POST http://localhost:8080/login -H "Content-Type: application/json" -d "{\"username\":\"你的账号\",\"password\":\"你的密码\"}"`，返回 JSON 中的 `data.token` 即 JWT
   - 携带令牌访问接口：`curl http://localhost:8080/depts -H "token: <上一步的JWT>"`（除 /login 外所有接口都需要）
   - 上传测试：`curl -F "file=@图片.jpg" http://localhost:8080/upload -H "token: <JWT>"`，返回 JSON 中的 `data` 即文件访问 URL

## 配置说明

配置文件为 `src/main/resources/application.yml`：

- **数据源**：`jdbc:mysql://localhost:3306/tlias`，用户名/密码 `root/123456` —— **本地开发默认值，请勿用于生产环境**
- **上传限制**：单文件 10MB、单请求 100MB
- **MyBatis**：开启驼峰映射，SQL 输出到控制台（开发用）
- **PageHelper**：`reasonable: true`（页码越界自动修正）、MySQL 方言

### 阿里云 OSS 配置

OSS 相关配置**硬编码在** `src/main/java/com/itheima/utils/AliyunOSSOperator.java` 中，未提取到配置文件：

| 配置项 | 当前值 | 说明 |
|---|---|---|
| endpoint | `https://oss-cn-shanghai.aliyuncs.com` | 上海地域访问域名 |
| bucketName | `java-web-buwlc` | 作者个人 Bucket，**换账号必须改** |
| region | `cn-shanghai` | Bucket 所在地域 |

- **凭据安全**：AccessKey 通过环境变量 `ALIBABA_CLOUD_ACCESS_KEY_ID` / `ALIBABA_CLOUD_ACCESS_KEY_SECRET` 提供（`EnvironmentVariableCredentialsProvider`），**代码和配置中不含密钥**；仓库的 `.gitignore` 也已加入 `.env`、`application-local.yml` 等防护规则，防止密钥文件被误提交
- 文件存储结构：`Bucket 根目录/yyyy/MM/uuid.后缀`（如 `2026/10/xxxxxxxx-xxxx.jpg`）

### JWT 登录令牌

- 工具类：`src/main/java/com/itheima/utils/JwtUtils.java`
- 签名算法：HS256；有效期 12 小时（`expire = 43200000L`）
- ⚠️ **签名密钥硬编码在 `JwtUtils.signKey`（`SVRIRUlNQQ==`，即 "itheima" 的 Base64）——教学用硬编码密钥，生产环境必须改为外部配置/环境变量**
- 旧版 jjwt 0.9.1 在 JDK 21 下需要 `jaxb-api` 依赖（pom.xml 已添加）

### ⚠️ 硬编码路径（换机器必须修改）

以下为本机绝对路径，clone 到其他机器后**必须改为本机实际路径**，否则相关功能无法工作：

| 位置 | 路径 | 用途 |
|---|---|---|
| `logback.xml` 的 `FileNamePattern` | `E:/CodeHTML-CSS/log/` | 滚动文件日志目录 |
| `src/test/java/com/itheima/Demo.java` | `E:\CodeJava\web-ai-project02\images\0f7b82fb...jpg` | OSS 上传演示类读取的本地文件（仅演示用） |

另外，`UploadController.java` 中的 `UPLOAD_DIR`（`E:\CodeJava\web-ai-project02\images\`）是**已废弃的死代码**——本地存盘上传已被 OSS 上传替代，旧实现以注释形式保留（课程教学代码），无需修改。

## 项目结构

```
tlias-web-management/
├── pom.xml
├── mvnw / mvnw.cmd            # Maven wrapper
└── src/
    ├── main/
    │   ├── java/com/itheima/
    │   │   ├── TliasWebManagementApplication.java   # 启动类
    │   │   ├── controller/     # DeptController、EmpController、UploadController
    │   │   ├── service/        # 业务接口 + impl/ 实现（事务在 ServiceImpl）
    │   │   ├── mapper/         # MyBatis Mapper 接口
    │   │   ├── pojo/           # Dept、Emp、EmpExpr、EmpLog、EmpQueryParam、PageResult、Result
    │   │   ├── utils/          # AliyunOSSOperator（OSS）、JwtUtils（JWT）、CurrentHolder（ThreadLocal）
    │   │   ├── config/         # WebConfig（拦截器注册）
    │   │   ├── filter/         # DemoFilter、TokenFilter（过滤器，未启用）
    │   │   ├── interceptor/    # DemoInterceptor（未启用）、TokenInterceptor（JWT 鉴权，已启用）
    │   │   ├── anno/           # @Log（操作日志注解）
    │   │   ├── aop/            # OperationLogAspect、RecordTimeAspect（切面）
    │   │   └── exception/      # GlobalExceptionHandler、BusinessException（全局异常）
    │   └── resources/
    │       ├── application.yml
    │       ├── logback.xml
    │       ├── com/itheima/mapper/   # EmpMapper.xml（动态 SQL）、EmpExprMapper.xml
    │       └── static/upload.html    # 上传接口测试页
    └── test/java/com/itheima/
        ├── TliasWebManagementApplicationTests.java   # 冒烟测试
        ├── LogTest.java        # 日志测试
        ├── TestJwt.java        # JWT 生成/解析测试
        ├── Demo.java           # OSS 上传演示（main 方法）
        └── Example.java        # OSS 列举 Bucket 演示（main 方法）
```

## 已知限制

- 鉴权已启用（`TokenInterceptor` 拦截除 /login 外的所有请求），但实现较简单：无权限分级/角色控制；`TokenFilter` 过滤器版本与 `DemoFilter`/`DemoInterceptor` 演示组件保留未启用
- `operate_log` 表**无建表 SQL**，需自行建表（字段见数据库章节）；未建表时 @Log 接口正常返回但日志写入会失败
- `OperationLogAspect` 的 `result.toString()` 在接口返回 null 时会抛 NPE（业务已执行，仅操作日志丢失）
- JWT 签名密钥硬编码在 `JwtUtils.signKey`（教学用，生产必须改）
- 数据库密码明文写在 `application.yml` 中
- `EmpServiceImpl.update` 修改员工的三步操作（更新基本信息 → 删除工作经历 → 重新插入）**未加 `@Transactional`**，中途失败会留下部分数据（非原子）
- OSS 的 endpoint/bucket/region 硬编码在 Java 代码中，未提取到配置文件
- 全局异常处理器为 catch-all 简单实现：透传 `e.getMessage()`，异常无消息时响应 msg 为 null；堆栈仅打印到控制台
- 不包含 Redis / AI 相关代码（与仓库名不符，见文首说明）

## 更新日志

### v1.0（2026-10-04）——最终版

- **JWT 鉴权正式启用**：`TokenInterceptor` 注册进 WebConfig，拦截除 `/login` 外所有请求，解析 token 并将员工 id 存入 ThreadLocal（`CurrentHolder`）；无效令牌返回 401
- **AOP 操作日志**：新增 `@Log` 注解与 `OperationLogAspect` 切面，自动记录操作人/参数/返回值/耗时到 `operate_log` 表（已标注 POST /clazzs、POST /depts、GET /depts/{id}、POST /students、DELETE /students/{ids} 五个接口）
- **AOP 耗时统计**：新增 `RecordTimeAspect`，统计 Service 层方法执行耗时
- **依赖**：+ spring-boot-starter-aspectj、spring-boot-starter-actuator

### v0.5（2026-10-03）

- **JWT 登录令牌**：`POST /login` 校验成功后签发 JWT（HS256，有效期 12 小时，携带 id/username），`LoginInfo.token` 不再为 null
- **过滤器/拦截器**：新增 `DemoFilter`、`DemoInterceptor` 教学演示组件；新增 `TokenFilter`、`TokenInterceptor` 令牌校验实现（**当前未启用**，见已知限制）
- **依赖**：+ jjwt 0.9.1、jaxb-api 2.3.1（解决旧版 JJWT 在 JDK 21 下缺 JAXB 类的问题）
- **测试**：新增 `TestJwt`（JWT 生成/解析测试）

### v0.4（2026-10-02）

- **员工登录**：新增 `POST /login` 用户名密码校验，返回 `LoginInfo`（token 暂未实现）
- **班级管理补全**：新增全部查询、详情、新增、修改、删除接口；删除前校验班下学员数，有学员时抛业务异常拒绝删除
- **学员管理**：新增 `GET/POST/PUT/DELETE /students` 全套接口（分页条件查询、批量删除）与 `PUT /students/violation/{id}/{score}` 违纪扣分
- **报表扩展**：新增 `GET /report/studentDegreeData`（学员学历分布）、`GET /report/studentCountData`（班级学员人数）
- **Cookie/Session 教学演示**：新增 `/c1`、`/c2`（Cookie 读写）与 `/s1`、`/s2`（Session 存取）
- **异常处理**：新增自定义业务异常 `BusinessException`；全局异常消息由固定文案改为透传异常内容

### v0.3（2026-10-01）

- **班级管理**：新增 `GET /clazzs` 条件分页查询（名称模糊、开班日期区间筛选），SQL 联查班主任姓名并用 CASE 计算开班状态（未开班/在读中/已结课）
- **报表统计**：新增 `GET /report/empJobData`（职位分布）与 `GET /report/empGenderData`（性别分布），输出 ECharts 饼图数据格式
- **学员实体**：新增 `Student` 实体（暂无接口使用，后续课程预备）

### v0.2（2026-10-01）

- **文件上传切换至阿里云 OSS**：新增 `AliyunOSSOperator` 封装（`alibabacloud-oss-v2 0.6.0`），对象名按 `yyyy/MM/uuid.后缀` 组织，上传成功返回文件访问 URL；凭据通过环境变量 `ALIBABA_CLOUD_ACCESS_KEY_ID` / `ALIBABA_CLOUD_ACCESS_KEY_SECRET` 提供，代码不含密钥；旧本地存盘实现注释保留
- **员工查询回显与修改**：新增 `GET /emps/{id}`（含工作经历，resultMap 一对多映射）与 `PUT /emps`（基本信息 + 工作经历重写）
- **全局异常处理器**：新增 `GlobalExceptionHandler`（`@RestControllerAdvice`），统一返回 `Result.error`
- 新增 OSS 演示类 `Demo.java`、`Example.java`

### v0.1（2026-09-30）

- 初始版本：部门管理（增删改查）、员工分页条件查询/新增/批量删除、本地磁盘文件上传、事务与审计日志（`REQUIRES_NEW`）、统一响应 `Result`
