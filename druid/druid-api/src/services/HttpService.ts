import axios, { AxiosRequestConfig } from "axios";
import { APILogger } from "./ApiLogger";

/**
 * It provides the service to interact with the an external system.
 */
export class HttpService {
    public static fetch(url: string, method: AxiosRequestConfig["method"], query?: any): Promise<any> {
        return new Promise((resolve, reject) => {
            APILogger.log("URL IS: " + url);
            axios({
                data: query,
                headers: { "Content-Type": "application/json" },
                method,
                url,
            }).then((res) => {
                resolve(res.data);
            }).catch((error) => {
                APILogger.error(error);
                reject(error);
            });
        });
    }
}
