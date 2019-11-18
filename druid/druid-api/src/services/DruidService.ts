import async from "asyncawait/async";
import await from "asyncawait/await";
import {IValidationResponse} from "../models/models";
import {ILimits} from "../models/models";
import { HttpService } from "./HttpService";
import { ValidationService } from "./ValidationService";

export class DruidService {
    private limits: ILimits;
    private httpService: HttpService;
    constructor(limits: ILimits, httpService: HttpService) {
        this.limits = limits;
        this.httpService = httpService;
    }

    public validate() {
        return async((request: any, response: any, next: any) => {
            const result: IValidationResponse = ValidationService.validate(request, this.limits);
            if (result.status) { next(); } else { response.send(result); response.end(); }
        });
    }

    public fetch() {
        return async(async (query: any) => {
            const result = await this.httpService.fetch(query);
            console.log("yes i..m");
            return result;
        });
    }
}
