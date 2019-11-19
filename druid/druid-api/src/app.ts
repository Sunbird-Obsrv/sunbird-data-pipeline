import bodyParser from "body-parser";
import express, { response } from "express";
import HttpStatus from "http-status-codes";
import { config } from "./configs/config";
import { DruidService } from "./services/DruidService";
import { HttpService } from "./services/HttpService";

const app = express();
const endPoint = config.apiEndPoint;
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

const httpService = new HttpService(config.druidHost, config.druidEndPoint, config.druidPort);
const druidService = new DruidService(config.limits, httpService);

app.post(endPoint, (requestObj, responseObj, next) => {
  druidService.validate()(requestObj.body, responseObj, next);
}, (requestObj, responseObj) => {
  druidService.fetch()(requestObj.body)
    .then((data) => {
      responseObj.status(HttpStatus.OK).json(data);
      responseObj.end();
    })
    .catch((err) => {
      responseObj.status(HttpStatus.INTERNAL_SERVER_ERROR);
      responseObj.send(err);
      responseObj.end();
    });
});

/**
 * Listen the server to config.port
 */
app.listen(config.apiPort, (err) => {
  if (err) {
    return console.error(err);
  }
  return console.log(`server is listening on ${config.apiPort}`);
});

export default app;
