FROM circleci/node:8.11
MAINTAINER "Manjunath Davanam" "manjunathd@ilimi.in"
USER root
WORKDIR /opt/druid-proxy-api/
COPY ./ .
RUN npm install
RUN npm install -g typescript@3.8.3
RUN tsc

FROM node:8.11-slim
MAINTAINER "Manjunath Davanam" "manjunathd@Ilimi.in"
RUN mkdir -p /home/sunbird/druid-proxy-api
WORKDIR /home/sunbird/druid-proxy-api
COPY --from=0 /opt/druid-proxy-api /home/sunbird/druid-proxy-api
WORKDIR /home/sunbird/druid-proxy-api/
CMD ["node", "dist/app.js"]
