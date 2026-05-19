#!/bin/sh
# Substitute environment variables in the template
envsubst '${PORT} ${PRODUCT_SERVICE_URL} ${ORDER_SERVICE_URL}' < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf

# Start Nginx
nginx -g 'daemon off;'
