FROM node:24

WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install

COPY . .
ENV SMTP_HOST=localhost
ENV SMTP_PORT=1025
ENV MAILHOG=http://localhost:8025

EXPOSE 3000

CMD ["node", "app.js"]
