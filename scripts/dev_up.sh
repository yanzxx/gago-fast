#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="${ROOT_DIR}/.dev/logs"
RUN_DIR="${ROOT_DIR}/.dev/run"
BACKEND_PID_FILE="${RUN_DIR}/backend.pid"
FRONTEND_PID_FILE="${RUN_DIR}/frontend.pid"

mkdir -p "${LOG_DIR}" "${RUN_DIR}"

export PATH="$HOME/.local/bin:$PATH"

BACKEND_PORT="${BACKEND_PORT:-82}"
FRONTEND_PORT="${FRONTEND_PORT:-81}"
PG_HOST="${PG_HOST:-127.0.0.1}"
PG_PORT="${PG_PORT:-5432}"

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
  kill -0 "${pid}" >/dev/null 2>&1
}

start_pg() {
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
  if [ -f "${BACKEND_PID_FILE}" ] && is_pid_running "$(cat "${BACKEND_PID_FILE}")"; then
    info "后端已在运行 (PID: $(cat "${BACKEND_PID_FILE}"))"
    return 0
  fi

  : > "${BACKEND_LOG}"
  info "启动后端 (端口 ${BACKEND_PORT})"

  (
    cd "${ROOT_DIR}"
    nohup mvn -s settings.xml -pl snowy-web-app -am spring-boot:run \
      -DskipTests \
      -Dspring-boot.run.jvmArguments="-Dserver.port=${BACKEND_PORT}" \
      >>"${BACKEND_LOG}" 2>&1 &
    echo $! > "${BACKEND_PID_FILE}"
  )
}

start_frontend() {
  if [ -f "${FRONTEND_PID_FILE}" ] && is_pid_running "$(cat "${FRONTEND_PID_FILE}")"; then
    info "前端已在运行 (PID: $(cat "${FRONTEND_PID_FILE}"))"
    return 0
  fi

  : > "${FRONTEND_LOG}"
  info "启动前端 (端口 ${FRONTEND_PORT})"

  (
    cd "${ROOT_DIR}/snowy-web"
    if [ ! -d node_modules ]; then
      info "检测到前端依赖未安装，执行 npm i"
      npm i >>"${FRONTEND_LOG}" 2>&1
    fi
    nohup npm run dev -- --host 0.0.0.0 --port "${FRONTEND_PORT}" \
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

  if [ -z "${backend_pid}" ] || ! is_pid_running "${backend_pid}"; then
    err "后端启动失败，日志: ${BACKEND_LOG}"
    exit 1
  fi
  if [ -z "${frontend_pid}" ] || ! is_pid_running "${frontend_pid}"; then
    err "前端启动失败，日志: ${FRONTEND_LOG}"
    exit 1
  fi

  info "启动完成"
  echo "  - PostgreSQL: ${PG_HOST}:${PG_PORT}"
  echo "  - Backend   : http://localhost:${BACKEND_PORT}"
  echo "  - Frontend  : http://localhost:${FRONTEND_PORT}"
  echo
  echo "日志文件:"
  echo "  - ${BACKEND_LOG}"
  echo "  - ${FRONTEND_LOG}"
  echo
  echo "停止命令: ./scripts/dev_down.sh"
}

main() {
  require_cmd java
  require_cmd mvn
  require_cmd node
  require_cmd npm
  require_cmd psql
  require_cmd pg_isready

  start_pg
  start_backend
  start_frontend
  check_started
}

main "$@"
