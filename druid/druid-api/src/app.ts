/**
 * @author Manjunatha Davanam <manjunathd@ilimi.in>
 */

import bodyParser from "body-parser";
import express from "express";
import HttpStatus from "http-status-codes";
import { config } from "./configs/config";
import { APILogger } from "./services/ApiLogger";
import { DruidService } from "./services/DruidService";
import { HttpService } from "./services/HttpService";

const app = express();
/**
 * Proxy API EndPoint Lists
 */
const endPoint = config.apiEndPoint;

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

/**
 * Creating a HTTP Service Instance to invoke the external system.
 */
const httpService = new HttpService(config.druidHost, Number(config.druidPort));

/**
 * Creating a DruidService Instance to facilitate to filter and validate the query.
 */
const druidService = new DruidService({ limits: config.limits }, httpService);

/**
 * API to query the data from the  External Source(druid)
 * @param - endPoint - "/druid/v2/*"
 * @param - requestObj - reqest object
 * Method Type - POST
 */
app.post(`${endPoint}/*`, (requestObj, responseObj, next) => {
  druidService.validate()(requestObj, responseObj, next, "/sql");
}, (requestObj, responseObj) => {
  druidService.fetch()(requestObj.url, "POST", requestObj.body)
    .then((data) => {
      responseObj.status(HttpStatus.OK).json(data);
      responseObj.end();
    })
    .catch((err) => {
      responseObj.status(HttpStatus.INTERNAL_SERVER_ERROR).json(err.message);
      responseObj.end();
    });
});

/**
 * API to get the list of data source which are available
 * @param - endPoint - "/druid/v2/*"
 * Method Type - GET
 */
app.get("/*", (requestObj, responseObj) => {
  druidService.fetch()(requestObj.url, "GET", undefined)
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
