FROM node:20-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

# Copy source and build
COPY . .
RUN npm run build

FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/templates/default.conf.template

ENV PORT=8080
ENV API_URL=http://api:8080
ENV DNS_RESOLVER="8.8.8.8 8.8.4.4"
EXPOSE 8080