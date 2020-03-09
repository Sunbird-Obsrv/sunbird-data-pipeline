/**
 * @author Manjunatha Davanam <manjunathd@ilimi.in>
 */

import bodyParser from "body-parser";
import express, { response } from "express";
import HttpStatus from "http-status-codes";
import { config } from "./configs/config";
import { APILogger } from "./services/ApiLogger";
import { DruidService } from "./services/DruidService";
import { HttpService } from "./services/HttpService";

const app = express();
const endPoint = config.apiEndPoint;
const sqlQueryEndPoint = config.druidSqlEndPoint;
const dataSourceEndPoint = config.druidDataSourceEndPoint;
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

/**
 * Creating a DruidService Instance to facilitate to filter and validate the query.
 */
const druidService = new DruidService({ limits: config.limits }, HttpService);

/**
 *
 */
app.post(endPoint, (requestObj, responseObj, next) => {
  druidService.validate()(requestObj.body, responseObj, next);
}, (requestObj, responseObj) => {
  druidService.fetch()(`${config.druidHost}:${config.druidPort}${config.druidEndPoint}`, "POST", requestObj.body)
    .then((data) => {
      responseObj.status(HttpStatus.OK).json(data);
      responseObj.end();
    })
    .catch((err) => {
      console.log("error" + err);
      responseObj.status(HttpStatus.INTERNAL_SERVER_ERROR).json(err.message);
      responseObj.end();
    });
});

/**
 *
 */
app.post(sqlQueryEndPoint, (requestObj, responseObj, next) => {
  druidService.validateKey()(requestObj.headers.authorization, responseObj, next);
}, (requestObj, responseObj) => {
  druidService.fetch()(`${config.druidHost}:${config.druidPort}${config.druidSqlEndPoint}`, "POST", requestObj.body)
    .then((data) => {
      responseObj.status(HttpStatus.OK).json(data);
      responseObj.end();
    })
    .catch((err) => {
      console.log("error" + err);
      responseObj.status(HttpStatus.INTERNAL_SERVER_ERROR).json(err.message);
      responseObj.end();
    });
});

/**
 * Method to get
 *
 */
app.get(dataSourceEndPoint, (requestObj, responseObj, next) => {
  druidService.fetch()(`${config.druidHost}:${config.druidPort}${config.druidDataSourceEndPoint}`, "GET", undefined)
    .then((data) => {
      responseObj.status(HttpStatus.OK).json(data);
      responseObj.end();
    }).catch((err) => {
      responseObj.status(HttpStatus.INTERNAL_SERVER_ERROR).json(err.message);
      responseObj.end();
    });
});

/**
 * Listen the server to config.port
 */
app.listen(Number(config.apiPort), (err, res) => {
  if (err) {
    return APILogger.error("Proxy API server is not running" + err);
  }
  APILogger.log(`server is listening on ${config.apiPort}`);
});

// Exporting app for the testing purpose.
export default app;
