FROM eclipse-temurin:11-jre-jammy
ENV TZ=Asia/Shanghai
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

RUN set -eux; \
    apt-get update; \
    for i in 1 2 3 4 5; do \
      DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends --fix-missing nginx supervisor curl && break; \
      echo "apt 安装失败，准备第 ${i} 次重试"; \
      apt-get update; \
      sleep 5; \
    done; \
    rm -rf /var/lib/apt/lists/*; \
    mkdir -p /data/web/build /app /app-log /var/log/supervisor

# 前端资源（由本地 npm build 生成）
COPY snowy-web/dist/ /data/web/build/
# 使用项目原有 nginx 主配置，覆盖站点配置以适配同容器后端
COPY docker/nginx/nginx.conf /etc/nginx/nginx.conf
COPY snowy-web/etc/nginx/mime.types /etc/nginx/mime.types
COPY docker/nginx/default.conf /etc/nginx/conf.d/default.conf

# 后端 Jar（由本地 maven package 生成）
COPY snowy-web-app/target/snowy-web-app-2.0.0.jar /app/gago-snowy.jar

# 进程托管
COPY docker/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

EXPOSE 80 82

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
