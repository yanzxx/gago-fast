#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN_DIR="${ROOT_DIR}/.dev/run"
BACKEND_PID_FILE="${RUN_DIR}/backend.pid"
FRONTEND_PID_FILE="${RUN_DIR}/frontend.pid"
BACKEND_PORT="${BACKEND_PORT:-8082}"
FRONTEND_PORT="${FRONTEND_PORT:-5173}"

is_pid_running() {
  local pid="$1"
  if [ -z "${pid}" ] || ! [[ "${pid}" =~ ^[0-9]+$ ]]; then
    return 1
  fi
  kill -0 "${pid}" >/dev/null 2>&1
}

stop_by_pid_file() {
  local name="$1"
  local pid_file="$2"

  if [ ! -f "${pid_file}" ]; then
    echo "[INFO] ${name}: 未运行"
    return 0
  fi

  local pid
  pid="$(cat "${pid_file}" 2>/dev/null || true)"

  if is_pid_running "${pid}"; then
    kill "${pid}" >/dev/null 2>&1 || true
    sleep 1
    if is_pid_running "${pid}"; then
      kill -9 "${pid}" >/dev/null 2>&1 || true
    fi
    echo "[INFO] ${name}: 已停止 (PID ${pid})"
  else
    echo "[INFO] ${name}: 进程不存在，清理 PID 文件"
  fi

  rm -f "${pid_file}"
}

stop_by_port() {
  local name="$1"
  local port="$2"
  local pids
  pids="$(lsof -tiTCP:"${port}" -sTCP:LISTEN -n -P 2>/dev/null || true)"
  if [ -z "${pids}" ]; then
    echo "[INFO] ${name}: 端口 ${port} 无监听"
    return 0
  fi

  echo "[INFO] ${name}: 清理端口 ${port} 监听进程 (${pids//$'\n'/, })"
  while IFS= read -r pid; do
    [ -z "${pid}" ] && continue
    kill "${pid}" >/dev/null 2>&1 || true
  done <<< "${pids}"
  sleep 1
  while IFS= read -r pid; do
    [ -z "${pid}" ] && continue
    if is_pid_running "${pid}"; then
      kill -9 "${pid}" >/dev/null 2>&1 || true
    fi
  done <<< "${pids}"
}

stop_by_pid_file "Backend" "${BACKEND_PID_FILE}"
stop_by_pid_file "Frontend" "${FRONTEND_PID_FILE}"
stop_by_port "Backend" "${BACKEND_PORT}"
stop_by_port "Frontend" "${FRONTEND_PORT}"

echo "[INFO] 完成"
