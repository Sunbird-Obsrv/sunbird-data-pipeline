import axios, { AxiosRequestConfig } from "axios";
import * as requestService from "request";
import { config } from "../configs/config";
import { APILogger } from "./ApiLogger";

/**
 * It provides the service to interact with the an external system.
 */
export class HttpService {
    // private port: number;
    // private endPoint: string;
    // private host: string;
    // private method: AxiosRequestConfig["method"]
    // constructor(host: string, endPoint: string, port = Number(config.druidPort), method: AxiosRequestConfig["method"]) {
    //     this.port = port;
    //     this.endPoint = endPoint;
    //     this.host = host;
    //     this.method = method;
    // }
    public static fetch(api: string, method: AxiosRequestConfig["method"], query?: any): Promise<any> {
        return new Promise((resolve, reject) => {
            //const URL = this.host + ":" + this.port + this.endPoint;
            console.log("URL" + api)
            console.log("method" + method)
            //APILogger.log("URL IS: " + URL);
            axios({
                data: query,
                headers: { "Content-Type": "application/json" },
                method: method,
                url: api,
            }).then((res) => {
                console.log("data" + res.data)
                resolve(res.data);
            }).catch((error) => {
                console.log("error is" + error)
                APILogger.error(error);
                reject(error);
            });
        });
    }
}
