# Stage 1: Build the React app
FROM node:20-alpine AS builder

WORKDIR /app

COPY package.json ./
RUN npm install

COPY public ./public
COPY src ./src

ARG REACT_APP_BASE_URL=http://localhost:8080
ENV REACT_APP_BASE_URL=$REACT_APP_BASE_URL

RUN npm run build

# Stage 2: Serve with nginx
FROM nginx:alpine

COPY --from=builder /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
