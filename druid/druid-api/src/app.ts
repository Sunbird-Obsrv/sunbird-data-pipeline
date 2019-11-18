import express, { response } from "express";
import { config } from "./configs/config";
import { DruidService } from "./services/DruidService";
import { HttpService } from "./services/HttpService";

const app = express();
import bodyParser from "body-parser";
import { request } from "https";

const port = config.port;
const endPoint = config.endpoint;
const host = config.host;
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

const httpService = new HttpService(host, endPoint, port);
const druidService = new DruidService(JSON.stringify(config.limits), httpService);

app.post(endPoint, (requestObj, responseObj, next) => {
  druidService.validate()(requestObj.body, responseObj, next);
}, (requestObj, responseObj) => {
  druidService.fetch(requestObj.body)
    .then((data) => {
      console.log(data);
      responseObj.status(200).json(data);
    })
    .catch((err) => {
      responseObj.send(err);
    });
});

/**
 * Listen the server to config.port
 */
app.listen(port, (err) => {
  if (err) {
    return console.error(err);
  }
  return console.log(`server is listening on ${port}`);
});
