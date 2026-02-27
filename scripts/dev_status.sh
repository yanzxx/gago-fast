#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN_DIR="${ROOT_DIR}/.dev/run"
LOG_DIR="${ROOT_DIR}/.dev/logs"

BACKEND_PID_FILE="${RUN_DIR}/backend.pid"
FRONTEND_PID_FILE="${RUN_DIR}/frontend.pid"
BACKEND_LOG="${LOG_DIR}/backend.log"
FRONTEND_LOG="${LOG_DIR}/frontend.log"

BACKEND_PORT="${BACKEND_PORT:-8082}"
FRONTEND_PORT="${FRONTEND_PORT:-5173}"
PG_HOST="${PG_HOST:-pgm-8vb5g50kv356g56r0o.pgsql.zhangbei.rds.aliyuncs.com}"
PG_PORT="${PG_PORT:-1921}"
PG_DB_NAME="${PG_DB_NAME:-liudayu_ks}"
PG_USER="${PG_USER:-liudayu_ks}"

print_line() {
  echo "------------------------------------------------------------"
}

pid_status() {
  local name="$1"
  local pid_file="$2"
  if [ ! -f "${pid_file}" ]; then
    echo "${name}: NOT_STARTED (no pid file)"
    return 0
  fi
  local pid
  pid="$(cat "${pid_file}")"
  if kill -0 "${pid}" >/dev/null 2>&1; then
    echo "${name}: RUNNING (PID ${pid})"
  else
    echo "${name}: STOPPED (stale PID ${pid})"
  fi
}

port_status() {
  local name="$1"
  local port="$2"
  if lsof -iTCP:"${port}" -sTCP:LISTEN -n -P >/dev/null 2>&1; then
    echo "${name} Port ${port}: LISTENING"
  else
    echo "${name} Port ${port}: NOT_LISTENING"
  fi
}

http_status() {
  local name="$1"
  local url="$2"
  local code
  local err_file="/tmp/dev_status_http_err.$$"
  code="$(curl -sS --max-time 5 -o /tmp/dev_status_http.out -w "%{http_code}" "${url}" 2>"${err_file}" || true)"
  if [ "${code}" = "200" ]; then
    echo "${name}: OK (HTTP 200)"
  else
    local err_msg
    err_msg="$(cat "${err_file}" 2>/dev/null || true)"
    if [ -n "${err_msg}" ]; then
      echo "${name}: FAIL (HTTP ${code:-N/A}) - ${err_msg}"
    else
      echo "${name}: FAIL (HTTP ${code:-N/A})"
    fi
  fi
  rm -f "${err_file}"
}

db_status() {
  if pg_isready -h "${PG_HOST}" -p "${PG_PORT}" -U "${PG_USER}" -d "${PG_DB_NAME}" >/dev/null 2>&1; then
    echo "PostgreSQL ${PG_HOST}:${PG_PORT}/${PG_DB_NAME}: READY"
  else
    echo "PostgreSQL ${PG_HOST}:${PG_PORT}/${PG_DB_NAME}: NOT_READY"
  fi
}

echo "Dev Status @ $(date '+%Y-%m-%d %H:%M:%S')"
print_line
pid_status "Backend" "${BACKEND_PID_FILE}"
pid_status "Frontend" "${FRONTEND_PID_FILE}"
print_line
port_status "Backend" "${BACKEND_PORT}"
port_status "Frontend" "${FRONTEND_PORT}"
print_line
http_status "Backend Health" "http://127.0.0.1:${BACKEND_PORT}/actuator/health"
http_status "Frontend Home" "http://127.0.0.1:${FRONTEND_PORT}/"
print_line
db_status
print_line

if [ -f "${BACKEND_LOG}" ]; then
  echo "Backend Log (tail -n 20):"
  tail -n 20 "${BACKEND_LOG}" || true
else
  echo "Backend Log: ${BACKEND_LOG} (not found)"
fi

print_line

if [ -f "${FRONTEND_LOG}" ]; then
  echo "Frontend Log (tail -n 10):"
  tail -n 10 "${FRONTEND_LOG}" || true
else
  echo "Frontend Log: ${FRONTEND_LOG} (not found)"
fi
