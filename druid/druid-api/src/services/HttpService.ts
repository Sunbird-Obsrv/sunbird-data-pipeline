import axios from "axios";
import * as requestService from "request";
import { config } from "../configs/config";

/**
 * It provides the service to interact with the an external system.
 */
export class HttpService {
    private port: number;
    private endPoint: string;
    private host: string;
    constructor(host: string, endPoint: string, port = config.druidPort) {
        this.port = port;
        this.endPoint = endPoint;
        this.host = host;
    }
    public fetch(query: any): Promise<any> {
        return new Promise((resolve, reject) => {
            const URL = this.host + ":" + this.port + this.endPoint;
            console.log("URL" + URL);
            axios({
                data: query,
                headers: { "Content-Type": "application/json" },
                method: "POST",
                url: URL,
            }).then((res) => {
                resolve(res.data);
            }).catch((error) => {
                console.error(error);
                reject(error);
            });
        });
    }
}
