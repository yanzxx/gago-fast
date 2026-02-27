#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN_DIR="${ROOT_DIR}/.dev/run"
BACKEND_PID_FILE="${RUN_DIR}/backend.pid"
FRONTEND_PID_FILE="${RUN_DIR}/frontend.pid"

stop_by_pid_file() {
  local name="$1"
  local pid_file="$2"

  if [ ! -f "${pid_file}" ]; then
    echo "[INFO] ${name}: 未运行"
    return 0
  fi

  local pid
  pid="$(cat "${pid_file}")"

  if kill -0 "${pid}" >/dev/null 2>&1; then
    kill "${pid}" >/dev/null 2>&1 || true
    sleep 1
    if kill -0 "${pid}" >/dev/null 2>&1; then
      kill -9 "${pid}" >/dev/null 2>&1 || true
    fi
    echo "[INFO] ${name}: 已停止 (PID ${pid})"
  else
    echo "[INFO] ${name}: 进程不存在，清理 PID 文件"
  fi

  rm -f "${pid_file}"
}

stop_by_pid_file "Backend" "${BACKEND_PID_FILE}"
stop_by_pid_file "Frontend" "${FRONTEND_PID_FILE}"

echo "[INFO] 完成"
