# meiracle-cloud

> 基于开源框架 [RuoYi-Cloud-Plus](https://gitee.com/dromara/RuoYi-Cloud-Plus) / [RuoYi-Cloud-Plus-UniApp](https://gitee.com/dromara/RuoYi-Cloud-Plus-UniApp) 改造的**多仓库模板工程**。
>
> 本仓库仅包含**通用能力沉淀 + 业务样板（demo）+ 监控样板（monitor）+ 中间件部署**，用于快速克隆出新的微服务业务系统。所有业务字段均为占位演示，不含任何真实业务/隐私数据。

## 版权与致谢

- 底层能力（MyBatis-Plus 封装、Redis/Redisson 封装、Dubbo、Nacos 接入等）派生自 **RuoYi-Cloud-Plus**，其原始版权归 [dromara](https://gitee.com/dromara) 所有，遵循 **MIT / Apache-2.0** 双协议。
- 本模板在原始实现基础上进行了包名收敛（`org.dromara.*` → `cn.meiracle.*`）、模块精简（仅保留 core / redis / mysql / dubbo / nacos 五项通用能力，移除文件/OSS/非必要子系统）与分层规范统一。
- 使用时请遵守 RuoYi-Cloud-Plus 的 MIT / Apache-2.0 协议要求，保留原始版权声明（见 [LICENSE](./LICENSE)）。

---

## 1. 项目定位与多仓库结构

`meiracle-cloud` 是一个**根模板仓库**，内含 4 个相互独立的 Maven 工程（各自拥有独立根 `pom.xml`，不聚合为单一 reactor）。新建业务系统时，建议将 4 个目录分别克隆为独立仓库，或整体克隆后按需拆分。

```
meiracle-cloud/
├── meiracle-common/      # 通用能力库（5 个模块，需 mvn install 到本地/私服）
│   ├── core/             # 核心：常量、异常、工具、BaseEntity、json 封装
│   ├── redis/            # Redisson 封装（读取 spring.data.redis.*）
│   ├── mysql/            # MyBatis-Plus 封装（BaseMapperPlus / 自动填充）
│   ├── dubbo/            # Dubbo 接入封装
│   └── nacos/            # Nacos 占位配置（NacosProperties，无业务逻辑）
├── meiracle-demo/        # 业务样板：演示一套完整业务链路（v3.1 分层）
├── meiracle-monitor/     # 监控/变动追踪样板：真实对接 demo（非空壳）
├── meiracle-deploy/      # 中间件编排 + 库表初始化 + Nacos 配置 + 部署脚本
└── README.md
```

| 工程 | 包名 | 说明 |
|------|------|------|
| meiracle-common | `cn.meiracle.{core,redis,mysql,dubbo,nacos}` | 被 demo / monitor 依赖的通用能力 |
| meiracle-demo | `cn.meiracle.demo` | 业务开发起点样板 |
| meiracle-monitor | `cn.meiracle.monitor` | 监控/变动追踪起点样板 |
| meiracle-deploy | — | 仅部署产物，不含 Java 业务代码 |

> **依赖方式**：`meiracle-demo` / `meiracle-monitor` 通过 `cn.meiracle:meiracle-*:1.0.0` 依赖 common 的 5 个能力模块。本地开发需先 `cd meiracle-common && mvn -DskipTests install`；团队共享可推送至 Nexus 私服（见 `meiracle-deploy/docker-compose.yml` 中的 nexus 服务）。

---

## 2. 分层架构规范（v3.1）

业务工程（demo / monitor）统一采用以下分层，**从 controller 到 provider 的调用链必须自上而下，禁止跨层反向依赖**：

```
controller        # 出入参、鉴权转发（REST 端点）
   └─ biz          # 业务编排 + @Transactional（组合 service/dao，事务边界）
        └─ service (+ impl)   # 领域服务（单实体逻辑，继承 ServiceImpl）
             └─ dao / mapper   # 数据访问（BaseMapperPlus<T, V>）
domain            # 实体（继承 BaseEntity，含审计字段）
export            # 跨模块契约：api(接口) + model(DTO)，供 provider/consumer 共享
provider/remote   # 远程能力暴露（@DubboService 实现 export.api）
task              # 定时任务（@Scheduled）
common/constants  # 常量
client            # 出站 HTTP 调用（demo -> monitor 变动上报）
```

**关键约束**

- 事务一律落在 `biz` 层；`service` 层不持有跨实体事务。
- 跨服务调用优先走 `export` 契约 + Dubbo（`@DubboService` 在 `provider/remote`，`@DubboReference` 在消费方）；HTTP 仅用于轻量通知（见 §5）。
- 实体字段统一继承 `cn.meiracle.mybatis.core.domain.BaseEntity`（createDept / createBy / createTime / updateBy / updateTime 自动填充）。

---

## 3. 从 demo 复制出新业务（教程）

1. **复制工程**：将 `meiracle-demo` 复制为 `meiracle-<你的业务>`（如 `meiracle-order`）。
2. **改包名**：全局把 `cn.meiracle.demo` → `cn.meiracle.<业务>`；把 `application.yml` 的 `spring.application.name` 改为 `meiracle-<业务>`。
3. **改端口与上下文**：`server.port` / `server.servlet.context-path`（如 `9202` / `/order`）；Dubbo 协议端口避免冲突（如 `20882`）。
4. **写实体与链路**：以 `DemoThing` 为模板，新建 `YourEntity`（`domain`）→ `XxxMapper`（`dao/mapper`，继承 `BaseMapperPlus`）→ `XxxService/Impl`（`service`）→ `XxxBiz`（`biz`，编排 + 事务）→ `XxxController`（`controller`）。
5. **暴露远程能力**：若需被其他服务调用，在 `export/api` 定义接口、`provider/remote` 用 `@DubboService` 实现；消费方通过 `export` 依赖 + `@DubboReference` 引入。
6. **依赖 common**：在 `pom.xml` 引入所需能力模块（core 必选，按需要 redis/mysql/dubbo/nacos）。
7. **建表**：在 `meiracle-deploy/init/init.sql` 追加本业务的建表语句（参考 `demo_thing`）。

> 遵循上述结构即可保证与 `meiracle-monitor` 的变动追踪天然兼容（见 §5）。

---

## 4. 中间件部署

详见 [`meiracle-deploy/README.md`](./meiracle-deploy/README.md)。要点：

```bash
cd meiracle-deploy
cp .env.example .env        # 编辑其中所有 CHANGE_ME_* 为真实值
bash scripts/start-all.sh   # 启动 mysql/redis/nacos/minio/nexus/seata
bash nacos/import.sh        # 可选：导入 Nacos 配置
bash scripts/deploy-demo.sh # 构建并启动 demo / monitor
```

`init/init.sql` 在 MySQL 首次启动时自动建库建表（`meiracle`、`meiracle_monitor`）。

---

## 5. monitor ↔ demo 连接关系（重要）

`meiracle-monitor` **不是空壳**，它通过两条真实链路与 `meiracle-demo` 互通，构成「变动追踪 + 健康采集」闭环：

### 链路 A：demo → monitor（变动上报，HTTP）
- `meiracle-demo` 在 `DemoThingBiz` 发生**新增（type=1）/ 状态变更（type=2）**时，通过 `client/MonitorClient`（RestTemplate）向
  `POST ${monitor.base-url}/monitor/change-record` 上报变动事件。
- `meiracle-monitor` 的 `ChangeRecordController` 接收并落库到 `monitor_change_record`（before/after 快照 JSON）。
- 失败优雅降级：上报异常仅记日志，不影响主流程。

### 链路 B：monitor → demo（健康/数据采集，HTTP 轮询）
- `meiracle-monitor` 的 `task/DemoProbeTask` 每 30s 通过 `RestTemplate` 轮询：
  - `GET ${demo.base-url}/demo/actuator/health` —— 探活；
  - `GET ${demo.base-url}/demo/demo-thing/list` —— 采集 demo 数据用于监控展示。
- 异常同样优雅降级，避免探测抖动影响 monitor。

### 配置对应
| 调用方 | 配置项 | 默认值 |
|--------|--------|--------|
| demo → monitor | `monitor.base-url` | `http://${MONITOR_HOST:127.0.0.1}:9211` |
| monitor → demo | `demo.base-url` | `http://${DEMO_HOST:127.0.0.1}:9201` |

### 验证互通
1. 启动中间件与两个服务（见 §4）。
2. 调用 `POST http://127.0.0.1:9201/demo/demo-thing`（新增），随后 `GET http://127.0.0.1:9211/monitor/change-record?bizDomain=demo_thing` 应能看到刚写入的变动记录。
3. 观察 `meiracle-monitor` 日志，应每 30s 打印一次从 demo 采集到的健康/数据信息。

> 两条链路均为**模板演示级**实现，生产可替换为消息队列（Kafka/RocketMQ）解耦上报，或接入 Prometheus 拉取 metrics。

---

## 6. 连接信息均为占位符（必读）

⚠️ **本仓库所有数据库连接、Redis、Nacos、MinIO、Seata、Nexus 的地址 / 账号 / 密码均为占位符**，统一采用以下形式：

- IP/域名：`127.0.0.1` / `localhost`
- 账号：`nacos` / `meiracle`（演示用，非真实）
- 密码：`CHANGE_ME_*` 或环境变量（如 `${DB_PASSWORD:CHANGE_ME_db_pwd}`）
- 端口：默认演示端口

**部署前请替换为真实值**，推荐通过以下方式注入，避免硬编码：

- 环境变量（已在各 `application.yml` 中以 `${...:默认值}` 形式预留）；或
- Nacos 配置中心（`meiracle-deploy/nacos/configs/` 提供样例）；或
- 密钥管理系统（Vault / KMS）。

本模板**不包含任何真实生产凭据、内网 IP、私有服务名或隐私数据**。

---

## 7. 许可证

本项目沿用 RuoYi-Cloud-Plus 的 **MIT / Apache-2.0** 双协议。详见 [LICENSE](./LICENSE)。
使用、修改、分发时请保留原始版权与许可声明。

---

## 8. 快速开始（本地）

```bash
# 1) 构建通用能力库
cd meiracle-common && mvn -DskipTests clean install && cd ..

# 2) 启动中间件
cd meiracle-deploy && cp .env.example .env && bash scripts/start-all.sh && cd ..

# 3) 启动业务与监控
bash meiracle-deploy/scripts/deploy-demo.sh

# 4) 验证
curl http://127.0.0.1:9201/demo/demo-thing
curl http://127.0.0.1:9211/monitor/change-record?bizDomain=demo_thing
```

> 需要 JDK 17、Maven 3.8+、Docker（含 compose 插件）。
