#Node를 사용해서 빌드한다
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

RUN npm run build

#nginx를 사용해서 빌드된 파일을 제공한다
FROM nginx:alpine

# ginx 기본 설정 덮어쓰기 (필요하면 nginx.conf 파일을 추가로 작성)
COPY --from=builder /app/build /usr/share/nginx/html

#포트 오픈
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
