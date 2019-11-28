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
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

/**
 * Creating a HTTP Service Instance to invoke the external system.
 */
const httpService = new HttpService(config.druidHost, config.druidEndPoint, Number(config.druidPort));

/**
 * Creating a DruidService Instance to facilitate to filter and validate the query.
 */
const druidService = new DruidService({ limits: config.limits }, httpService);

app.post(endPoint, (requestObj, responseObj, next) => {
  druidService.validate()(requestObj.body, responseObj, next);
}, (requestObj, responseObj) => {
  druidService.fetch()(requestObj.body)
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
