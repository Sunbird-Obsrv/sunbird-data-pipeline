import async from "asyncawait/async";
import * as await from "asyncawait/await";
import {IValidationResponse} from "../models/ErrorResponse";
import { HttpService } from "./HttpService";
import { ValidationService } from "./ValidationService";

export class DruidService {
    private limits: string;
    private httpService: HttpService;
    constructor(limits: string, httpService: HttpService) {
        this.limits = limits;
        this.httpService = httpService;
    }

    public validate() {
        return async((request: any, response: any, next: any) => {
            const result: IValidationResponse = ValidationService.validate(request);
            if (result.status) { next(); } else { response.send(result); }
        });
    }

    public async fetch(query: any): Promise<any> {
        return await this.httpService.fetch(query);
    }
}
