FROM har.gagogroup.cn/base-software/node:18.20.8-alpine AS build

WORKDIR /build

COPY ./ .

RUN npm set registry https://registry.npmmirror.com && npm i

RUN npm run build

FROM har.gagogroup.cn/base-software/nginx:1.28.0-alpine AS release

COPY etc/nginx/nginx.conf /etc/nginx/nginx.conf
COPY etc/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY etc/nginx/upstream.conf /etc/nginx/conf.d/upstream.conf
COPY etc/nginx/mime.types /etc/nginx/mime.types

COPY --from=build /build/dist/ /data/web/build/
