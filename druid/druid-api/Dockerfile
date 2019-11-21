FROM node:8.11-alpine
MAINTAINER "Manjunath Davanam" "manjunathd@Ilimi.in"
RUN apk update \
    && apk add unzip \
    && apk add curl \
    && adduser -u 1001 -h /home/sunbird/ -D sunbird \
    && apk add --update ca-certificates  \
    && apk add --update ca-certificates openssl
USER sunbird
RUN mkdir -p /home/sunbird/druid-proxy-api
WORKDIR /home/sunbird/druid-proxy-api
COPY ./druid-proxy-api.zip  /home/sunbird/druid-proxy-api/
RUN unzip /home/sunbird/druid-proxy-api/druid-proxy-api.zip
RUN ls -all /home/sunbird/druid-proxy-api
WORKDIR /home/sunbird/druid-proxy-api/druid-proxy-api/
CMD ["node", "dist/app.js", "&"]