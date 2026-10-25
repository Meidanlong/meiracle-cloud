#!/usr/bin/env bash
# ============================================================================
# 将 ./configs 下的 YAML 配置批量导入 Nacos（OpenAPI v1 /cs/configs）。
# 依赖 curl、jq。需提前启动 nacos 服务。
#
# 用法：
#   NACOS_ADDR=127.0.0.1:8848 NACOS_USERNAME=nacos NACOS_PASSWORD=CHANGE_ME \
#     ./nacos/import.sh
# 默认值均为占位，请按实际 Nacos 地址与账号替换。
# ============================================================================
set -euo pipefail

NACOS_ADDR="${NACOS_ADDR:-127.0.0.1:8848}"
NACOS_USERNAME="${NACOS_USERNAME:-nacos}"
NACOS_PASSWORD="${NACOS_PASSWORD:-CHANGE_ME_nacos_pwd}"
GROUP="${NACOS_GROUP:-DEFAULT_GROUP}"
TENANT="${NACOS_TENANT:-}"          # 命名空间 ID，留空表示 public
CONFIG_DIR="$(cd "$(dirname "$0")" && pwd)/configs"

# 若开启鉴权，先登录获取 token
TOKEN=""
if command -v jq >/dev/null 2>&1; then
  TOKEN=$(curl -s -X POST "http://${NACOS_ADDR}/nacos/v1/auth/login" \
    -d "username=${NACOS_USERNAME}" -d "password=${NACOS_PASSWORD}" | jq -r '.accessToken // empty' 2>/dev/null)
fi

post_config() {
  local data_id="$1" file="$2"
  local content; content=$(cat "$file")
  local args=(
    -X POST "http://${NACOS_ADDR}/nacos/v1/cs/configs"
    -d "dataId=${data_id}"
    -d "group=${GROUP}"
    -d "type=yaml"
    -d "content=${content}"
    --data-urlencode "username=${NACOS_USERNAME}"
    --data-urlencode "password=${NACOS_PASSWORD}"
  )
  [ -n "${TENANT}" ] && args+=(-d "tenant=${TENANT}")
  [ -n "${TOKEN}" ]  && args+=(-d "accessToken=${TOKEN}")

  local http_code
  http_code=$(curl -s -o /tmp/nacos_import_resp -w '%{http_code}' "${args[@]}")
  if [ "$http_code" = "200" ] && grep -q 'true' /tmp/nacos_import_resp; then
    echo "[OK]   导入成功: ${data_id}"
  else
    echo "[FAIL] 导入失败: ${data_id} (HTTP ${http_code}) -> $(cat /tmp/nacos_import_resp)"
    return 1
  fi
}

shopt -s nullglob
for f in "${CONFIG_DIR}"/*.yaml; do
  [ -e "$f" ] || continue
  data_id="$(basename "$f" .yaml)"
  post_config "$data_id" "$f" || true
done
echo "Nacos 配置导入完成（地址 ${NACOS_ADDR}）。"
