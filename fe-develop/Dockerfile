# Build Stage
FROM registry.hpicorp.tk/base/nodejs:mdms_latest as build-stage
LABEL maintainer="NeroWu <amdcctan@gmail.com>"
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Production Stage
FROM registry.hpicorp.tk/base/nginx:latest as prod-stage
LABEL maintainer="NeroWu <amdcctan@gmail.com>"
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]