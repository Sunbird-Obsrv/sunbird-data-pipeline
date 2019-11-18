import axios from "axios";
import * as requestService from "request";
import { config } from "../configs/config";

export class HttpService {
    private port: number;
    private endPoint: string;
    private host: string;
    constructor(host: string, endPoint: string, port = config.port) {
        this.port = port;
        this.endPoint = endPoint;
        this.host = "http://11.2.1.20";
    }
    public fetch(request: any): Promise<any> {
        return new Promise((resolve, reject) => {
            const URL = this.host + ":" + this.port + this.endPoint;
            axios({
                data: request,
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
