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
| 其他 | Lombok、SLF4J + Logback |
| 构建 | Maven（自带 wrapper，无需全局安装） |

## 功能列表

- **部门管理**：查询全部、按 ID 查询、新增、修改、删除
- **员工管理**：分页查询（默认第 1 页 / 每页 10 条）、按姓名/性别/入职日期区间筛选、新增员工（含工作经历批量保存）、批量删除（连带删除工作经历）
- **事务与审计**：员工写操作记入 `emp_log` 审计表；日志写入使用 `REQUIRES_NEW` 独立事务，业务回滚时审计记录仍然保留
- **文件上传**：multipart 上传，UUID 重命名防止文件名冲突，单文件上限 10MB
- **统一响应**：所有接口返回 `{code, msg, data}`，`code=1` 表示成功

## API 端点

| 方法 | 路径 | 参数 | 说明 |
|---|---|---|---|
| GET | `/depts` | - | 查询所有部门 |
| GET | `/depts/{id}` | 路径参数 id | 按 ID 查询部门 |
| POST | `/depts` | body: Dept JSON | 新增部门 |
| PUT | `/depts` | body: Dept JSON | 修改部门 |
| DELETE | `/depts?id=1` | 查询参数 id | 删除部门（注意：id 在查询参数中） |
| GET | `/emps` | page、pageSize、name、gender、begin、end | 分页 + 条件查询员工 |
| POST | `/emps` | body: Emp JSON | 新增员工（可含工作经历列表 exprList） |
| DELETE | `/emps?ids=1,2,3` | 查询参数 ids | 批量删除员工 |
| POST | `/upload` | form-data：name、age、file | 上传文件，返回 UUID 文件名 |

响应示例：

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

### 步骤

1. 创建数据库 `tlias` 并建表（表结构见上文）
2. 按需修改 `src/main/resources/application.yml` 中的数据库用户名/密码（默认 `root/123456`）
3. 修改硬编码路径（见下方配置说明）
4. 启动项目：
   - Windows：`mvnw.cmd spring-boot:run`
   - Linux/Mac：`./mvnw spring-boot:run`
   - 或在 IDEA 中直接运行 `TliasWebManagementApplication`
5. 验证：访问 `http://localhost:8080/depts`（默认端口 8080）

## 配置说明

配置文件为 `src/main/resources/application.yml`：

- **数据源**：`jdbc:mysql://localhost:3306/tlias`，用户名/密码 `root/123456` —— **本地开发默认值，请勿用于生产环境**
- **上传限制**：单文件 10MB、单请求 100MB
- **MyBatis**：开启驼峰映射，SQL 输出到控制台（开发用）
- **PageHelper**：`reasonable: true`（页码越界自动修正）、MySQL 方言

### ⚠️ 硬编码路径（运行前必须修改）

以下两处为作者本机绝对路径，clone 到其他机器后**必须改为本机实际路径**，否则文件上传会写盘失败、文件日志无法写入：

| 位置 | 路径 | 用途 |
|---|---|---|
| `UploadController.java` 的 `UPLOAD_DIR` | `E:\CodeJava\web-ai-project02\images\` | 上传文件保存目录 |
| `logback.xml` 的 `FileNamePattern` | `E:/CodeHTML-CSS/log/` | 滚动文件日志目录 |

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
    │   │   └── pojo/           # Dept、Emp、EmpExpr、EmpLog、EmpQueryParam、PageResult、Result
    │   └── resources/
    │       ├── application.yml
    │       ├── logback.xml
    │       ├── com/itheima/mapper/   # EmpMapper.xml（动态 SQL）、EmpExprMapper.xml
    │       └── static/upload.html    # 上传接口测试页
    └── test/java/com/itheima/        # 冒烟测试
```

## 已知限制

- 无登录鉴权/权限控制，接口全部开放，仅用于课程学习
- 数据库密码明文写在 `application.yml` 中
- 无全局异常处理器
- 不包含 Redis / AI 相关代码（与仓库名不符，见文首说明）
