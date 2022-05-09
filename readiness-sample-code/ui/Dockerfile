#stage 1
FROM node:16 as node
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --prod
#stage 2
FROM nginx:alpine
COPY --from=node /app/dist/ui /usr/share/nginx/html
