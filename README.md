# tlias-web-management 员工管理系统（后端）

黑马程序员（itheima）JavaWeb 课程项目 —— tlias 智能学习辅助系统中**员工管理模块的后端服务**，纯 REST API 项目，无前端页面（仓库内 `upload.html` 仅用于文件上传接口的手动测试）。

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
| 其他 | Lombok、SLF4J + Logback |
| 构建 | Maven（自带 wrapper，无需全局安装） |

## 功能列表

- **部门管理**：查询全部、按 ID 查询、新增、修改、删除
- **员工管理**：分页查询（默认第 1 页 / 每页 10 条）、按姓名/性别/入职日期区间筛选、新增员工（含工作经历批量保存）、按 ID 查询详情回显（含工作经历）、修改员工（基本信息 + 工作经历重写）、批量删除（连带删除工作经历）
- **事务与审计**：员工写操作记入 `emp_log` 审计表；日志写入使用 `REQUIRES_NEW` 独立事务，业务回滚时审计记录仍然保留
- **文件上传（阿里云 OSS）**：multipart 上传至阿里云 OSS，对象名按 `yyyy/MM/uuid.后缀` 组织，上传成功返回文件访问 URL
- **统一响应**：所有接口返回 `{code, msg, data}`，`code=1` 表示成功
- **全局异常处理**：`@RestControllerAdvice` 统一捕获未处理异常并返回 `Result.error`，异常堆栈不再直接暴露给调用方

## API 端点

| 方法 | 路径 | 参数 | 说明 |
|---|---|---|---|
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
| POST | `/upload` | form-data：file | 上传文件至阿里云 OSS，成功返回 `data = 文件访问 URL` |

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
   - 访问 `http://localhost:8080/depts`（默认端口 8080）
   - 上传测试：`curl -F "file=@图片.jpg" http://localhost:8080/upload`，返回 JSON 中的 `data` 即文件访问 URL

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
    │   │   ├── utils/          # AliyunOSSOperator（阿里云 OSS 上传封装）
    │   │   └── exception/      # GlobalExceptionHandler（全局异常处理）
    │   └── resources/
    │       ├── application.yml
    │       ├── logback.xml
    │       ├── com/itheima/mapper/   # EmpMapper.xml（动态 SQL）、EmpExprMapper.xml
    │       └── static/upload.html    # 上传接口测试页
    └── test/java/com/itheima/
        ├── TliasWebManagementApplicationTests.java   # 冒烟测试
        ├── LogTest.java        # 日志测试
        ├── Demo.java           # OSS 上传演示（main 方法）
        └── Example.java        # OSS 列举 Bucket 演示（main 方法）
```

## 已知限制

- 无登录鉴权/权限控制，接口全部开放，仅用于课程学习
- 数据库密码明文写在 `application.yml` 中
- `EmpServiceImpl.update` 修改员工的三步操作（更新基本信息 → 删除工作经历 → 重新插入）**未加 `@Transactional`**，中途失败会留下部分数据（非原子）
- OSS 的 endpoint/bucket/region 硬编码在 Java 代码中，未提取到配置文件
- 全局异常处理器为 catch-all 简单实现：所有异常统一返回「操作失败」，异常堆栈仅打印到控制台
- 不包含 Redis / AI 相关代码（与仓库名不符，见文首说明）
