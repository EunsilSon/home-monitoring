# 1단계 빌드 없음 → dist만 사용
FROM nginx:alpine

# 이미 로컬에 존재하는 dist를 Nginx html 디렉토리에 복사
COPY dist /usr/share/nginx/html
COPY ../nginx/nginx.conf /etc/nginx/conf.d/default.conf

# 필요하면 Nginx 기본 포트 노출
EXPOSE 80

# 컨테이너 시작 시 Nginx 실행
CMD ["nginx", "-g", "daemon off;"]
