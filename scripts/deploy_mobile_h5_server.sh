#!/usr/bin/env bash
set -euo pipefail

# 远端服务器配置：可通过环境变量覆盖
SERVER_HOST="${SERVER_HOST:-182.92.207.79}"
SERVER_USER="${SERVER_USER:-root}"
SERVER_PASSWORD="${SERVER_PASSWORD:-Yanzx19930622my}"

# 镜像仓库配置：当前脚本本身不推镜像，仅保留默认值用于统一环境参数
REGISTRY_HOST="${REGISTRY_HOST:-crpi-ere0kjidm57ziagk.cn-beijing.personal.cr.aliyuncs.com}"
REGISTRY_NAMESPACE="${REGISTRY_NAMESPACE:-aitest_1}"
REGISTRY_USERNAME="${REGISTRY_USERNAME:-dt_6368732917}"
REGISTRY_PASSWORD="${REGISTRY_PASSWORD:-Yanzx19930622my}"

# 移动端部署配置：可通过环境变量覆盖
REMOTE_BASE_DIR="${REMOTE_BASE_DIR:-/opt/gago/mobile}"
REMOTE_BACKUP_DIR="${REMOTE_BACKUP_DIR:-/opt/gago/mobile-backups}"
CONTAINER_NAME="${CONTAINER_NAME:-gago-mobile-h5}"
REMOTE_PORT="${REMOTE_PORT:-8081}"

# 本地构建配置
PROJECT_ROOT="${PROJECT_ROOT:-$(cd "$(dirname "$0")/.." && pwd)}"
MOBILE_DIR="${MOBILE_DIR:-${PROJECT_ROOT}/snowy-mobile}"
DIST_DIR="${DIST_DIR:-${MOBILE_DIR}/dist}"
PACKAGE_NAME="${PACKAGE_NAME:-gago-mobile-dist.tar.gz}"
PACKAGE_PATH="${PACKAGE_PATH:-/tmp/${PACKAGE_NAME}}"

if ! command -v sshpass >/dev/null 2>&1; then
  echo "缺少 sshpass，请先安装：macOS 可执行 brew install hudochenkov/sshpass/sshpass"
  exit 1
fi

if ! command -v npm >/dev/null 2>&1; then
  echo "缺少 npm，请先安装 Node.js 18+"
  exit 1
fi

if ! command -v tar >/dev/null 2>&1; then
  echo "缺少 tar，无法打包 dist"
  exit 1
fi

if [[ ! -d "${MOBILE_DIR}" ]]; then
  echo "未找到移动端目录: ${MOBILE_DIR}"
  exit 1
fi

echo "[1/6] 构建移动端 dist"
(
  cd "${MOBILE_DIR}"
  npm run build
)

if [[ ! -f "${DIST_DIR}/index.html" ]]; then
  echo "移动端构建失败：未找到 ${DIST_DIR}/index.html"
  exit 1
fi

echo "[2/6] 打包 dist"
tar czf "${PACKAGE_PATH}" -C "${DIST_DIR}" .
ls -lh "${PACKAGE_PATH}"

echo "[3/6] 上传构建包到 ${SERVER_USER}@${SERVER_HOST}"
sshpass -p "${SERVER_PASSWORD}" scp -o StrictHostKeyChecking=no "${PACKAGE_PATH}" \
  "${SERVER_USER}@${SERVER_HOST}:/tmp/${PACKAGE_NAME}"

echo "[4/6] 远端备份并替换静态资源"
sshpass -p "${SERVER_PASSWORD}" ssh -o StrictHostKeyChecking=no "${SERVER_USER}@${SERVER_HOST}" \
  "set -euo pipefail; \
   ts=\$(date +%Y%m%d%H%M%S); \
   mkdir -p '${REMOTE_BACKUP_DIR}'; \
   if [ -d '${REMOTE_BASE_DIR}/dist' ] && [ \"\$(find '${REMOTE_BASE_DIR}/dist' -mindepth 1 -maxdepth 1 | head -n 1)\" != '' ]; then \
     tar czf '${REMOTE_BACKUP_DIR}/dist-\${ts}.tar.gz' -C '${REMOTE_BASE_DIR}/dist' .; \
   fi; \
   rm -rf '${REMOTE_BASE_DIR}/dist.new'; \
   mkdir -p '${REMOTE_BASE_DIR}/dist.new'; \
   tar xzf '/tmp/${PACKAGE_NAME}' -C '${REMOTE_BASE_DIR}/dist.new'; \
   rm -rf '${REMOTE_BASE_DIR}/dist.prev'; \
   if [ -d '${REMOTE_BASE_DIR}/dist' ]; then mv '${REMOTE_BASE_DIR}/dist' '${REMOTE_BASE_DIR}/dist.prev'; fi; \
   mv '${REMOTE_BASE_DIR}/dist.new' '${REMOTE_BASE_DIR}/dist'; \
   echo 'backup='${REMOTE_BACKUP_DIR}'/dist-'\"\${ts}\"'.tar.gz'; \
   echo 'current='${REMOTE_BASE_DIR}'/dist';"

echo "[5/6] 重启移动端容器"
sshpass -p "${SERVER_PASSWORD}" ssh -o StrictHostKeyChecking=no "${SERVER_USER}@${SERVER_HOST}" \
  "set -euo pipefail; docker restart '${CONTAINER_NAME}' >/dev/null; docker ps --filter 'name=${CONTAINER_NAME}' --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'"

echo "[6/6] 校验移动端页面和接口"
sshpass -p "${SERVER_PASSWORD}" ssh -o StrictHostKeyChecking=no "${SERVER_USER}@${SERVER_HOST}" \
  "set -euo pipefail; \
   echo '--- index ---'; \
   curl -s -I --max-time 10 'http://127.0.0.1:${REMOTE_PORT}' | sed -n '1,8p'; \
   echo '--- captcha ---'; \
   curl -s -i --max-time 15 'http://127.0.0.1:${REMOTE_PORT}/api/auth/b/getPicCaptcha' | sed -n '1,20p';"

echo "移动端部署完成"
echo "访问地址: http://${SERVER_HOST}:${REMOTE_PORT}"
