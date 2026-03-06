#!/bin/sh
set -eu

MOBILE_API_UPSTREAM="${MOBILE_API_UPSTREAM:-gago-fast-server:82}"
export MOBILE_API_UPSTREAM

sed "s|\${MOBILE_API_UPSTREAM}|${MOBILE_API_UPSTREAM}|g" \
  /etc/nginx/conf.d/upstream.conf.template \
  > /etc/nginx/conf.d/upstream.conf

exec nginx -g 'daemon off;'
