FROM nginx:1.28-alpine

ENV TZ=Asia/Shanghai

COPY snowy-web/etc/nginx/nginx.conf /etc/nginx/nginx.conf
COPY snowy-web/etc/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY snowy-web/etc/nginx/upstream.conf /etc/nginx/conf.d/upstream.conf
COPY snowy-web/etc/nginx/mime.types /etc/nginx/mime.types
COPY snowy-web/dist/ /data/web/build/

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
