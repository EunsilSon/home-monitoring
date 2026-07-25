FROM nginx:alpine

COPY dist /usr/share/nginx/html
COPY ./nginx.conf.template /etc/nginx/conf.d/default.conf.template

EXPOSE 80

# API_KEY 환경 변수를 nginx 설정에 주입
CMD ["/bin/sh", "-c", "envsubst '${API_KEY}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
