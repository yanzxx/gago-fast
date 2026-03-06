FROM nginx:1.28-alpine

ENV TZ=Asia/Shanghai

COPY snowy-mobile/etc/nginx/nginx.conf /etc/nginx/nginx.conf
COPY snowy-mobile/etc/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY snowy-mobile/etc/nginx/upstream.conf.template /etc/nginx/conf.d/upstream.conf.template
COPY snowy-mobile/etc/nginx/mime.types /etc/nginx/mime.types
COPY snowy-mobile/docker-entrypoint.sh /docker-entrypoint.sh
COPY snowy-mobile/dist/ /data/web/build/

RUN chmod +x /docker-entrypoint.sh

EXPOSE 80

ENTRYPOINT ["/docker-entrypoint.sh"]
