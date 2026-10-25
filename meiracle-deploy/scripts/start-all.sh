#!/usr/bin/env bash
# ============================================================================
# 启动全部中间件（docker compose）。
# 首次运行会复制 .env.example 为 .env，请先修改其中所有 CHANGE_ME_* 为真实值。
# ============================================================================
set -euo pipefail
cd "$(dirname "$0")/.."

if [ ! -f .env ]; then
  echo "[INFO] 未找到 .env，已从 .env.example 复制，请编辑其中的 CHANGE_ME_* 凭据后再继续。"
  cp .env.example .env
  echo "[ACTION] 请先修改 .env 中的占位密码，然后重新执行本脚本。"
  exit 0
fi

echo "[INFO] 启动中间件（mysql / redis / nacos / minio / nexus / seata）..."
docker compose up -d

echo "[INFO] 等待 MySQL 健康..."
for i in $(seq 1 30); do
  if [ "$(docker inspect -f '{{.State.Health.Status}}' meiracle-mysql 2>/dev/null)" = "healthy" ]; then
    echo "[OK] MySQL 已就绪。"
    break
  fi
  sleep 3
done

echo "[DONE] 中间件启动完成。Nacos 控制台: http://127.0.0.1:8848/nacos"
echo "       可选：导入配置 -> bash nacos/import.sh"
