#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="${ROOT_DIR}/.dev/logs"
RUN_DIR="${ROOT_DIR}/.dev/run"
BACKEND_PID_FILE="${RUN_DIR}/backend.pid"
FRONTEND_PID_FILE="${RUN_DIR}/frontend.pid"

mkdir -p "${LOG_DIR}" "${RUN_DIR}"

export PATH="$HOME/.local/bin:$PATH"

BACKEND_PORT="${BACKEND_PORT:-8082}"
FRONTEND_PORT="${FRONTEND_PORT:-5173}"
FRONTEND_HOST="${FRONTEND_HOST:-127.0.0.1}"
START_BACKEND="${START_BACKEND:-true}"
START_FRONTEND="${START_FRONTEND:-true}"
PG_HOST="${PG_HOST:-127.0.0.1}"
PG_PORT="${PG_PORT:-5432}"
PG_DB_NAME="${PG_DB_NAME:-liudayu_ks}"
PG_USER="${PG_USER:-liudayu_ks}"
PG_PASSWORD="${PG_PASSWORD:-}"
PG_SCHEMA="${PG_SCHEMA:-yzx}"
PG_REMOTE_CHECK_STRICT="${PG_REMOTE_CHECK_STRICT:-false}"
MAVEN_REPO_LOCAL="${MAVEN_REPO_LOCAL:-${ROOT_DIR}/.m2/repository}"
BACKEND_JAR="${BACKEND_JAR:-${ROOT_DIR}/snowy-web-app/target/snowy-web-app-2.0.0.jar}"

BACKEND_LOG="${LOG_DIR}/backend.log"
FRONTEND_LOG="${LOG_DIR}/frontend.log"

err() {
  echo "[ERROR] $*" >&2
}

info() {
  echo "[INFO] $*"
}

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    err "缺少命令: $1"
    exit 1
  fi
}

is_pid_running() {
  local pid="$1"
  if [ -z "${pid}" ] || ! [[ "${pid}" =~ ^[0-9]+$ ]]; then
    return 1
  fi
  kill -0 "${pid}" >/dev/null 2>&1
}

pid_listening_on_port() {
  local port="$1"
  lsof -tiTCP:"${port}" -sTCP:LISTEN -n -P 2>/dev/null | head -n 1 || true
}

cleanup_pid_file_if_invalid() {
  local pid_file="$1"
  if [ ! -f "${pid_file}" ]; then
    return 0
  fi
  local pid
  pid="$(cat "${pid_file}" 2>/dev/null || true)"
  if ! is_pid_running "${pid}"; then
    rm -f "${pid_file}"
  fi
}

stop_port_listener_if_exists() {
  local port="$1"
  local name="$2"
  local pid
  pid="$(pid_listening_on_port "${port}")"
  if [ -z "${pid}" ]; then
    return 0
  fi

  info "${name} 端口 ${port} 已被占用，清理旧进程 PID ${pid}"
  kill "${pid}" >/dev/null 2>&1 || true
  sleep 1
  if is_pid_running "${pid}"; then
    kill -9 "${pid}" >/dev/null 2>&1 || true
    sleep 1
  fi
}

start_pg() {
  if [ "${PG_HOST}" != "127.0.0.1" ] && [ "${PG_HOST}" != "localhost" ]; then
    info "使用远程 PostgreSQL (${PG_HOST}:${PG_PORT})，执行连通性检查"
    local ok="0"
    local i
    for i in 1 2 3; do
      if pg_isready -h "${PG_HOST}" -p "${PG_PORT}" -U "${PG_USER}" -d "${PG_DB_NAME}" >/dev/null 2>&1; then
        ok="1"
        break
      fi
      sleep 1
    done
    if [ "${ok}" != "1" ]; then
      if [ "${PG_REMOTE_CHECK_STRICT}" = "true" ]; then
        err "远程 PostgreSQL 不可用或网络不可达: ${PG_HOST}:${PG_PORT}/${PG_DB_NAME}"
        exit 1
      fi
      info "远程 PostgreSQL 预检失败，继续尝试启动后端（strict=false）"
      return 0
    fi
    info "远程 PostgreSQL 可用 (${PG_HOST}:${PG_PORT})"
    return 0
  fi

  if command -v pg_isready >/dev/null 2>&1 && pg_isready -h "${PG_HOST}" -p "${PG_PORT}" >/dev/null 2>&1; then
    info "PostgreSQL 已在运行 (${PG_HOST}:${PG_PORT})"
    return 0
  fi

  if command -v pg_ctl >/dev/null 2>&1; then
    local data_dir=""
    for d in \
      "/opt/homebrew/var/postgres" \
      "/usr/local/var/postgres" \
      "$HOME/.local/var/postgres"; do
      if [ -d "${d}" ]; then
        data_dir="${d}"
        break
      fi
    done

    if [ -z "${data_dir}" ]; then
      err "未找到 PostgreSQL 数据目录，请先初始化数据库。"
      exit 1
    fi

    info "正在启动 PostgreSQL: ${data_dir}"
    pg_ctl -D "${data_dir}" -l "${LOG_DIR}/postgres.log" start >/dev/null
    sleep 2
  else
    err "未找到 pg_ctl，无法自动启动 PostgreSQL。"
    exit 1
  fi

  if ! pg_isready -h "${PG_HOST}" -p "${PG_PORT}" >/dev/null 2>&1; then
    err "PostgreSQL 启动失败，请查看 ${LOG_DIR}/postgres.log"
    exit 1
  fi

  info "PostgreSQL 启动成功 (${PG_HOST}:${PG_PORT})"
}

start_backend() {
  cleanup_pid_file_if_invalid "${BACKEND_PID_FILE}"

  if [ -f "${BACKEND_PID_FILE}" ]; then
    local old_pid
    old_pid="$(cat "${BACKEND_PID_FILE}")"
    if is_pid_running "${old_pid}" && [ "$(pid_listening_on_port "${BACKEND_PORT}")" = "${old_pid}" ]; then
      info "后端已在运行 (PID: ${old_pid})"
      return 0
    fi
    rm -f "${BACKEND_PID_FILE}"
  fi

  stop_port_listener_if_exists "${BACKEND_PORT}" "后端"

  : > "${BACKEND_LOG}"
  info "启动后端 (端口 ${BACKEND_PORT})"

  (
    cd "${ROOT_DIR}"
    if [ ! -f "${BACKEND_JAR}" ]; then
      err "后端 JAR 不存在: ${BACKEND_JAR}"
      err "请先执行: mvn -s settings.xml -Dmaven.repo.local=.m2/repository -DskipTests package"
      exit 1
    fi
    local java_cmd
    java_cmd="$(command -v java || true)"
    if [ -z "${java_cmd}" ]; then
      err "未找到 java 命令"
      exit 1
    fi
    local db_url="jdbc:p6spy:postgresql://${PG_HOST}:${PG_PORT}/${PG_DB_NAME}?currentSchema=${PG_SCHEMA}&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true&useInformationSchema=true"
    nohup "${java_cmd}" -jar "${BACKEND_JAR}" \
      --server.port="${BACKEND_PORT}" \
      --spring.datasource.dynamic.datasource.master.driver-class-name=com.p6spy.engine.spy.P6SpyDriver \
      --spring.datasource.dynamic.datasource.master.url="${db_url}" \
      --spring.datasource.dynamic.datasource.master.username="${PG_USER}" \
      --spring.datasource.dynamic.datasource.master.password="${PG_PASSWORD}" \
      >>"${BACKEND_LOG}" 2>&1 &
    echo $! > "${BACKEND_PID_FILE}"
  )
}

start_frontend() {
  cleanup_pid_file_if_invalid "${FRONTEND_PID_FILE}"

  if [ -f "${FRONTEND_PID_FILE}" ]; then
    local old_pid
    old_pid="$(cat "${FRONTEND_PID_FILE}")"
    if is_pid_running "${old_pid}" && [ "$(pid_listening_on_port "${FRONTEND_PORT}")" = "${old_pid}" ]; then
      info "前端已在运行 (PID: ${old_pid})"
      return 0
    fi
    rm -f "${FRONTEND_PID_FILE}"
  fi

  stop_port_listener_if_exists "${FRONTEND_PORT}" "前端"

  : > "${FRONTEND_LOG}"
  info "启动前端 (端口 ${FRONTEND_PORT})"

  (
    cd "${ROOT_DIR}/snowy-web"
    if [ ! -d node_modules ]; then
      info "检测到前端依赖未安装，执行 npm i"
      npm i >>"${FRONTEND_LOG}" 2>&1
    fi
    nohup npm run dev -- --host "${FRONTEND_HOST}" --port "${FRONTEND_PORT}" \
      >>"${FRONTEND_LOG}" 2>&1 &
    echo $! > "${FRONTEND_PID_FILE}"
  )
}

check_started() {
  sleep 3
  local backend_pid=""
  local frontend_pid=""

  if [ -f "${BACKEND_PID_FILE}" ]; then
    backend_pid="$(cat "${BACKEND_PID_FILE}")"
  fi
  if [ -f "${FRONTEND_PID_FILE}" ]; then
    frontend_pid="$(cat "${FRONTEND_PID_FILE}")"
  fi

  if [ "${START_BACKEND}" = "true" ]; then
    if [ -z "${backend_pid}" ] || ! is_pid_running "${backend_pid}"; then
      err "后端启动失败，日志: ${BACKEND_LOG}"
      tail -n 80 "${BACKEND_LOG}" || true
      exit 1
    fi
  fi
  if [ "${START_FRONTEND}" = "true" ]; then
    if [ -z "${frontend_pid}" ] || ! is_pid_running "${frontend_pid}"; then
      err "前端启动失败，日志: ${FRONTEND_LOG}"
      tail -n 80 "${FRONTEND_LOG}" || true
      exit 1
    fi
  fi

  # 后端进程存在不代表可用，进一步校验端口与健康接口
  if [ "${START_BACKEND}" = "true" ]; then
    local backend_ok="0"
    local i
    for i in $(seq 1 45); do
      if ! is_pid_running "${backend_pid}"; then
        err "后端进程已退出，日志: ${BACKEND_LOG}"
        tail -n 120 "${BACKEND_LOG}" || true
        exit 1
      fi
      local listening_pid
      listening_pid="$(pid_listening_on_port "${BACKEND_PORT}")"
      if [ -n "${listening_pid}" ] && [ "${listening_pid}" != "${backend_pid}" ]; then
        err "后端端口 ${BACKEND_PORT} 被其他进程占用 (PID ${listening_pid})"
        tail -n 120 "${BACKEND_LOG}" || true
        exit 1
      fi
      if curl -s "http://127.0.0.1:${BACKEND_PORT}/actuator/health" | grep -q "\"status\":\"UP\""; then
        backend_ok="1"
        break
      fi
      sleep 2
    done

    if [ "${backend_ok}" != "1" ]; then
      err "后端健康检查未通过，请查看日志: ${BACKEND_LOG}"
      tail -n 120 "${BACKEND_LOG}" || true
      exit 1
    fi
  fi

  info "启动完成"
  echo "  - PostgreSQL: ${PG_HOST}:${PG_PORT}"
  if [ "${START_BACKEND}" = "true" ]; then
    echo "  - Backend   : http://localhost:${BACKEND_PORT}"
  fi
  if [ "${START_FRONTEND}" = "true" ]; then
    echo "  - Frontend  : http://localhost:${FRONTEND_PORT}"
  fi
  echo
  echo "日志文件:"
  echo "  - ${BACKEND_LOG}"
  echo "  - ${FRONTEND_LOG}"
  echo
  echo "停止命令: ./scripts/dev_down.sh"
}

main() {
  require_cmd java
  require_cmd node
  require_cmd npm
  require_cmd psql
  require_cmd pg_isready

  start_pg
  if [ "${START_BACKEND}" = "true" ]; then
    start_backend
  fi
  if [ "${START_FRONTEND}" = "true" ]; then
    start_frontend
  fi
  check_started

  echo
  echo "当前后端数据库配置:"
  echo "  - host     : ${PG_HOST}"
  echo "  - port     : ${PG_PORT}"
  echo "  - database : ${PG_DB_NAME}"
  echo "  - schema   : ${PG_SCHEMA}"
  echo "  - username : ${PG_USER}"
}

main "$@"
