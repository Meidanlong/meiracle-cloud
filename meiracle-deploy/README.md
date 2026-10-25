# meiracle-deploy · 中间件部署

本目录提供 `meiracle-cloud` 所需的全套中间件一键编排，以及库表初始化、Nacos 配置导入和业务服务部署脚本。

> ⚠️ **所有连接信息与账号密码均为占位符（`CHANGE_ME_*` 或环境变量默认值）。部署前请务必替换为真实值，切勿将真实凭据提交到仓库。**

## 目录结构

```
meiracle-deploy/
├── docker-compose.yml      # mysql / redis / nacos / minio / nexus / seata 编排
├── .env.example            # 环境变量样例（复制为 .env 后修改）
├── init/
│   └── init.sql            # 建库建表（meiracle / meiracle_monitor）
├── nacos/
│   ├── config-import.yaml  # 配置导入清单（manifest）
│   ├── configs/            # 实际配置：common / demo / monitor
│   └── import.sh           # 批量导入 Nacos（OpenAPI）
└── scripts/
    ├── start-all.sh        # 启动全部中间件
    ├── stop-all.sh         # 停止并移除容器（保留数据卷）
    └── deploy-demo.sh      # 构建并启动 demo / monitor 两个业务服务
```

## 1. 启动中间件

```bash
cd meiracle-deploy
cp .env.example .env          # 然后编辑 .env 中所有 CHANGE_ME_* 为真实强密码
bash scripts/start-all.sh     # 启动 mysql/redis/nacos/minio/nexus/seata
```

容器启动后 MySQL 会自动执行 `init/init.sql` 完成建库建表。

## 2. 导入 Nacos 配置（可选）

```bash
NACOS_ADDR=127.0.0.1:8848 NACOS_USERNAME=nacos NACOS_PASSWORD=<真实密码> \
  bash nacos/import.sh
```

会把 `nacos/configs/` 下的三份配置发布到 Nacos 控制台（默认命名空间 public）。

## 3. 部署业务服务

`deploy-demo.sh` 会本地构建 `meiracle-common`（install 到本地仓库）、打包 `meiracle-demo` / `meiracle-monitor` 并用占位环境变量后台启动：

```bash
bash scripts/deploy-demo.sh
# 访问：
#   demo    : http://127.0.0.1:9201/demo
#   monitor : http://127.0.0.1:9211/monitor
```

> 生产环境建议改为 Docker 镜像 + 编排，或通过 Nacos 注入真实配置，而非脚本内硬编码占位变量。

## 4. 占位符对照（部署前必改）

| 项           | 占位符 / 默认值                  | 说明                         |
|--------------|--------------------------------|------------------------------|
| MySQL 密码   | `CHANGE_ME_mysql_root` / `CHANGE_ME_db_pwd` | root 与业务账号密码          |
| Redis 密码   | `CHANGE_ME_redis_pwd`          | `requirepass` 及客户端连接   |
| Nacos 凭据   | `CHANGE_ME_nacos_*`            | 鉴权身份 key/value 与 token  |
| MinIO 凭据   | `CHANGE_ME_minio_*`            | 对象存储 access/secret       |
| Nexus 密码   | `CHANGE_ME_nexus_admin`        | Maven 私服管理员密码         |
| Seata token  | `CHANGE_ME_seata_token`        | 分布式事务控制台 token       |

## 5. 停止

```bash
bash scripts/stop-all.sh        # 停止并移除容器，保留数据卷
# bash scripts/stop-all.sh -v   # （如需）连同数据卷一起清理，不可恢复
```
