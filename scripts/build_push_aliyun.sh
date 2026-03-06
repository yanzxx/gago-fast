#!/usr/bin/env bash
set -euo pipefail

REGISTRY_HOST="${REGISTRY_HOST:-crpi-ere0kjidm57ziagk.cn-beijing.personal.cr.aliyuncs.com}"
REGISTRY_NAMESPACE="${REGISTRY_NAMESPACE:-aitest_1}"
REGISTRY_USERNAME="${REGISTRY_USERNAME:-dt_6368732917}"
REGISTRY_PASSWORD="${REGISTRY_PASSWORD:-Yanzx19930622my}"
SERVICE="${SERVICE:-server}"
IMAGE_TAG="${IMAGE_TAG:-last}"
IMAGE_PLATFORM="${IMAGE_PLATFORM:-linux/amd64}"
BUILD_MODE="${BUILD_MODE:-local}"

SERVER_HOST="${SERVER_HOST:-182.92.207.79}"
SERVER_USER="${SERVER_USER:-root}"
SERVER_PASSWORD="${SERVER_PASSWORD:-Yanzx19930622my}"
REMOTE_BUILD_DIR="${REMOTE_BUILD_DIR:-/tmp/gago-fast-build}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "缺少命令: $1"
    exit 1
  fi
}

service_image_name() {
  case "${SERVICE}" in
    server)
      echo "gago-fast-server"
      ;;
    web)
      echo "gago-fast-web"
      ;;
    mobile-h5)
      echo "gago-fast-mobile-h5"
      ;;
    all-in-one)
      echo "gago-fast"
      ;;
    *)
      echo "不支持的 SERVICE: ${SERVICE}" >&2
      exit 1
      ;;
  esac
}

IMAGE_NAME="${IMAGE_NAME:-$(service_image_name)}"
FULL_IMAGE="${REGISTRY_HOST}/${REGISTRY_NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG}"

build_server_artifact() {
  echo "  - 后端构建：mvn clean package"
  rm -f "${REPO_ROOT}/snowy-web-app/target/snowy-web-app-2.0.0.jar"
  (
    cd "${REPO_ROOT}"
    mvn -s settings.xml -DskipTests clean package
  )
  [[ -f "${REPO_ROOT}/snowy-web-app/target/snowy-web-app-2.0.0.jar" ]] || {
    echo "后端 jar 构建失败：未找到 snowy-web-app/target/snowy-web-app-2.0.0.jar"
    exit 1
  }
}

build_web_artifact() {
  echo "  - Web 前端构建：snowy-web npm run build"
  rm -rf "${REPO_ROOT}/snowy-web/dist"
  (
    cd "${REPO_ROOT}/snowy-web"
    npm set registry https://registry.npmmirror.com
    npm i
    npm run build
  )
  [[ -f "${REPO_ROOT}/snowy-web/dist/index.html" ]] || {
    echo "Web dist 构建失败：未找到 snowy-web/dist/index.html"
    exit 1
  }
}

build_mobile_artifact() {
  echo "  - 移动端 H5 构建：snowy-mobile npm run build"
  rm -rf "${REPO_ROOT}/snowy-mobile/dist"
  (
    cd "${REPO_ROOT}/snowy-mobile"
    npm set registry https://registry.npmmirror.com
    npm i
    npm run build
  )
  [[ -f "${REPO_ROOT}/snowy-mobile/dist/index.html" ]] || {
    echo "移动端 dist 构建失败：未找到 snowy-mobile/dist/index.html"
    exit 1
  }
}

build_artifacts() {
  echo "[1/4] 构建 ${SERVICE} 所需产物"
  case "${SERVICE}" in
    server)
      build_server_artifact
      ;;
    web)
      build_web_artifact
      ;;
    mobile-h5)
      build_mobile_artifact
      ;;
    all-in-one)
      build_web_artifact
      build_server_artifact
      ;;
  esac
}

dockerfile_for_service() {
  case "${SERVICE}" in
    server)
      echo "server.Dockerfile"
      ;;
    web)
      echo "web.Dockerfile"
      ;;
    mobile-h5)
      echo "snowy-mobile/h5.Dockerfile"
      ;;
    all-in-one)
      echo "Dockerfile"
      ;;
  esac
}

archive_context() {
  local archive_path="$1"
  case "${SERVICE}" in
    server)
      (
        cd "${REPO_ROOT}"
        tar czf "${archive_path}" \
          server.Dockerfile \
          snowy-web-app/target/snowy-web-app-2.0.0.jar
      )
      ;;
    web)
      (
        cd "${REPO_ROOT}"
        tar czf "${archive_path}" \
          web.Dockerfile \
          snowy-web/dist \
          snowy-web/etc/nginx/nginx.conf \
          snowy-web/etc/nginx/default.conf \
          snowy-web/etc/nginx/upstream.conf \
          snowy-web/etc/nginx/mime.types
      )
      ;;
    mobile-h5)
      (
        cd "${REPO_ROOT}"
        tar czf "${archive_path}" \
          snowy-mobile/h5.Dockerfile \
          snowy-mobile/dist \
          snowy-mobile/docker-entrypoint.sh \
          snowy-mobile/etc/nginx/nginx.conf \
          snowy-mobile/etc/nginx/default.conf \
          snowy-mobile/etc/nginx/upstream.conf.template \
          snowy-mobile/etc/nginx/mime.types
      )
      ;;
    all-in-one)
      (
        cd "${REPO_ROOT}"
        tar czf "${archive_path}" \
          Dockerfile \
          docker \
          snowy-web/dist \
          snowy-web/etc/nginx/mime.types \
          snowy-web-app/target/snowy-web-app-2.0.0.jar
      )
      ;;
  esac
}

remote_build_and_push() {
  local archive_path dockerfile_path archive_size remote_size
  archive_path="$(mktemp /tmp/gago-fast-build.XXXXXX.tar.gz)"
  dockerfile_path="$(dockerfile_for_service)"

  echo "[2/4] 打包并上传 ${SERVICE} 上下文到远端 Docker"
  archive_context "${archive_path}"
  archive_size="$(wc -c < "${archive_path}" | tr -d ' ')"

  sshpass -p "${SERVER_PASSWORD}" ssh -o StrictHostKeyChecking=no "${SERVER_USER}@${SERVER_HOST}" \
    "rm -rf '${REMOTE_BUILD_DIR}' && mkdir -p '${REMOTE_BUILD_DIR}'"
  sshpass -p "${SERVER_PASSWORD}" ssh -o StrictHostKeyChecking=no "${SERVER_USER}@${SERVER_HOST}" \
    "cat > '${REMOTE_BUILD_DIR}/context.tar.gz'" < "${archive_path}"
  remote_size="$(sshpass -p "${SERVER_PASSWORD}" ssh -o StrictHostKeyChecking=no "${SERVER_USER}@${SERVER_HOST}" \
    "wc -c < '${REMOTE_BUILD_DIR}/context.tar.gz'" | tr -d '[:space:]')"
  if [[ "${archive_size}" != "${remote_size}" ]]; then
    echo "远端构建包大小不一致: local=${archive_size}, remote=${remote_size}"
    rm -f "${archive_path}"
    exit 1
  fi

  echo "[3/4] 远端构建镜像: ${FULL_IMAGE}"
  sshpass -p "${SERVER_PASSWORD}" ssh -o StrictHostKeyChecking=no "${SERVER_USER}@${SERVER_HOST}" "
    set -euo pipefail
    cd '${REMOTE_BUILD_DIR}'
    tar xzf context.tar.gz
    echo '${REGISTRY_PASSWORD}' | docker login --username='${REGISTRY_USERNAME}' --password-stdin '${REGISTRY_HOST}'
    docker build --platform '${IMAGE_PLATFORM}' -t '${FULL_IMAGE}' -f '${dockerfile_path}' .
    docker push '${FULL_IMAGE}'
    rm -rf '${REMOTE_BUILD_DIR}'
  "

  rm -f "${archive_path}"
}

local_build_and_push() {
  local dockerfile_path
  dockerfile_path="$(dockerfile_for_service)"

  echo "[2/4] 登录阿里云镜像仓库: ${REGISTRY_HOST}"
  echo "${REGISTRY_PASSWORD}" | docker login --username="${REGISTRY_USERNAME}" --password-stdin "${REGISTRY_HOST}"

  echo "[3/4] 本地构建镜像: ${FULL_IMAGE}"
  if docker buildx version >/dev/null 2>&1; then
    docker buildx build \
      --platform "${IMAGE_PLATFORM}" \
      -t "${FULL_IMAGE}" \
      -f "${REPO_ROOT}/${dockerfile_path}" \
      --push \
      "${REPO_ROOT}"
  else
    echo "未检测到 docker buildx，回退到本机构建"
    docker build -t "${FULL_IMAGE}" -f "${REPO_ROOT}/${dockerfile_path}" "${REPO_ROOT}"
    echo "[4/4] 推送镜像: ${FULL_IMAGE}"
    docker push "${FULL_IMAGE}"
  fi
}

build_artifacts

if [[ "${BUILD_MODE}" == "remote" ]]; then
  require_cmd sshpass
  remote_build_and_push
else
  require_cmd docker
  local_build_and_push
fi

echo "[4/4] 完成: ${FULL_IMAGE}"
