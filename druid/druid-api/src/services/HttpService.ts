import axios, { AxiosRequestConfig } from "axios";
import { config } from "../configs/config";
import { APILogger } from "./ApiLogger";

/**
 * It provides the service to interact with the an external system.
 */
export class HttpService {
    private port: number;
    private host: string;
    /**
     * Primary constructor of the HTTP Service
     * @param host - External System Address
     * @param port  - Endpoint Ststem Port
     */
    constructor(host: string, port = Number(config.druidPort)) {
        this.port = port;
        this.host = host;
    }

    /**
     * Method to fetch to data from the external system.
     * @param endPoint - API Endpoint (External system: Druid) 
     * @param method - [GET, POST, DELETE, PUT]
     * @param query - Optional - Request object 
     */
    public fetch(endPoint: string, method: AxiosRequestConfig["method"], query?: any): Promise<any> {
        return new Promise((resolve, reject) => {
            const URL = this.host + ":" + this.port + endPoint;
            axios({
                data: query,
                headers: { "Content-Type": "application/json" },
                method,
                url: URL,
            }).then((res) => {
                resolve(res.data);
            }).catch((error) => {
                APILogger.error(error);
                reject(error);
            });
        });
    }
}
