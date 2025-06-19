FROM node:16

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY . .
COPY docker.env .env

EXPOSE 3000

CMD ["node", "./bin/www"]