#!/usr/bin/env bash
# ============================================================================
# 构建并启动 meiracle-demo 与 meiracle-monitor（本机 Java 运行方式）。
# 前置：JDK 17、Maven 3.8+、中间件已通过 start-all.sh 启动。
#
# 流程：
#   1) mvn install meiracle-common（5 个能力模块打入本地仓库，供 demo/monitor 依赖）
#   2) mvn package meiracle-demo / meiracle-monitor，产出可执行 jar
#   3) 用占位环境变量启动两个服务（可按 .env 覆盖 DB_HOST / REDIS_PASSWORD 等）
#
# 注意：所有凭据均为占位，生产请通过真实环境变量或 Nacos 注入。
# ============================================================================
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT_DIR"

echo "[INFO] 1/3 构建 meiracle-common ..."
( cd meiracle-common && mvn -q -DskipTests clean install )

echo "[INFO] 2/3 打包 meiracle-demo / meiracle-monitor ..."
( cd meiracle-demo    && mvn -q -DskipTests clean package )
( cd meiracle-monitor && mvn -q -DskipTests clean package )

# 占位环境变量（部署前请替换为真实值，例如从 .env 读取）
export DB_HOST="${DB_HOST:-127.0.0.1}"
export DB_USER="${DB_USER:-meiracle}"
export DB_PASSWORD="${DB_PASSWORD:-CHANGE_ME_db_pwd}"
export REDIS_HOST="${REDIS_HOST:-127.0.0.1}"
export REDIS_PASSWORD="${REDIS_PASSWORD:-CHANGE_ME_redis_pwd}"
export NACOS_ADDR="${NACOS_ADDR:-127.0.0.1:8848}"
export NACOS_PASSWORD="${NACOS_PASSWORD:-CHANGE_ME_nacos_pwd}"
export MONITOR_HOST="${MONITOR_HOST:-127.0.0.1}"
export DEMO_HOST="${DEMO_HOST:-127.0.0.1}"

DEMO_JAR=$(ls meiracle-demo/target/meiracle-demo.jar 2>/dev/null || true)
MON_JAR=$(ls meiracle-monitor/target/meiracle-monitor.jar 2>/dev/null || true)

start_app() {
  local name="$1" jar="$2"
  if [ -z "$jar" ] || [ ! -f "$jar" ]; then
    echo "[FAIL] 未找到 $name 的 jar 包，构建可能失败。"
    return 1
  fi
  nohup java -jar "$jar" > "logs/${name}.log" 2>&1 &
  echo "[OK] 已后台启动 $name (pid $!) -> logs/${name}.log"
}

mkdir -p logs
echo "[INFO] 3/3 启动服务 ..."
start_app meiracle-demo    "$DEMO_JAR"
start_app meiracle-monitor "$MON_JAR"

echo "[DONE] demo  : http://127.0.0.1:9201/demo"
echo "       monitor: http://127.0.0.1:9211/monitor"
echo "       查看日志: tail -f logs/meiracle-demo.log"
