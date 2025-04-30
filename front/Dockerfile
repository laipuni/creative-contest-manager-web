# Node를 사용해서 빌드한다
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Nginx를 사용해서 빌드된 파일을 제공한다
FROM nginx:alpine

# nginx 기본 설정 덮어쓰기 (nginx.conf 포함)
COPY --from=builder /app/build /usr/share/nginx/html

# 커스텀 nginx 설정을 복사
COPY nginx/default.conf /etc/nginx/conf.d/default.conf

# 포트 오픈
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]
