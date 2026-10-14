# 用户管理与登录鉴权 Demo（Spring Boot 3）

一个展示**后端开发 + 部署运维**能力的完整小项目：用户注册/登录（JWT 鉴权）、用户 CRUD、Redis 缓存、容器化一键部署。

> 用这一个作品即可覆盖：Java 17 / Spring Boot 3 / MySQL 8 / Redis / JWT / Spring Security / Docker 部署。

---

## 技术栈

| 模块 | 技术 |
|---|---|
| 语言/框架 | Java 17, Spring Boot 3.2, Spring Web, Spring Data JPA |
| 安全 | Spring Security + JWT (jjwt) + BCrypt 密码加密 |
| 存储 | MySQL 8（业务数据）、Redis 7（缓存） |
| 部署 | Docker + docker-compose（多阶段构建、健康检查、非 root 运行） |

## 功能

- **注册**：用户名唯一、密码 BCrypt 加密存储、参数校验
- **登录**：校验密码 → 签发 JWT Token（默认 24h 过期）
- **鉴权**：除 `/api/auth/**` 外所有接口需携带 `Authorization: Bearer <token>`
- **用户管理**：查自己 / 列表 / 更新昵称邮箱 / 删除
- **缓存**：用户详情走 Redis 缓存（`@Cacheable`），降低 DB 压力
- **统一响应 & 全局异常**：`ApiResponse` 包裹 + `@RestControllerAdvice` 统一错误返回

## 目录结构

```
user-auth-demo
├── Dockerfile                # 多阶段构建，非 root 运行
├── docker-compose.yml        # 一键起 MySQL + Redis + 应用
├── pom.xml
└── src/main/java/com/demo/user/
    ├── UserAuthApplication.java
    ├── common/               # ApiResponse / BizException / 全局异常
    ├── config/               # SecurityConfig / JwtAuthFilter / RedisConfig
    ├── controller/           # AuthController / UserController
    ├── dto/                  # RegisterRequest / LoginRequest
    ├── entity/               # User
    ├── repository/           # UserRepository
    ├── service/              # AuthService / UserService
    └── util/                 # JwtUtil
```

## 快速启动

### 方式一：Docker 一键部署（推荐）

```bash
docker compose up -d --build
# 等待 mysql/redis 健康检查通过，应用即启动
```

### 方式二：本地运行

1. 本地起 MySQL（库名 `user_auth_demo`）和 Redis
2. 修改 `application.yml` 里的连接信息
3. 运行：

```bash
mvn clean package -DskipTests
java -jar target/user-auth-demo-1.0.0.jar
```

## 接口说明

Base URL：`http://localhost:8080`

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/api/auth/register` | 注册 | 否 |
| POST | `/api/auth/login` | 登录，返回 token | 否 |
| GET | `/api/users/me` | 查当前用户 | 是 |
| GET | `/api/users` | 用户列表 | 是 |
| PUT | `/api/users/me` | 更新昵称/邮箱 | 是 |
| DELETE | `/api/users/me` | 删除账号 | 是 |

### 调用示例

```bash
# 注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"123456","nickname":"Alice","email":"alice@example.com"}'

# 登录，拿到 token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"123456"}'

# 带 token 查自己
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <你的token>"
```

## 安全注意

- JWT 密钥、数据库密码通过**环境变量注入**，不要写死在代码里（生产必须覆盖默认值）
- 密码仅存 BCrypt 密文，不存明文
- Docker 镜像以**非 root 用户**运行

## 后续可扩展

- 刷新 Token / 登出（token 加入 Redis 黑名单）
- 邮箱验证码
- 分页查询、字段脱敏
- 接入 Prometheus 监控 + Grafana

---

*个人练习/求职作品。有问题欢迎交流。*
