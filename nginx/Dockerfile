FROM nginx:alpine

# Install envsubst
RUN apk add --no-cache gettext

COPY nginx.conf.template /etc/nginx/nginx.conf.template
COPY run.sh /run.sh
RUN chmod +x /run.sh

CMD ["/run.sh"]
