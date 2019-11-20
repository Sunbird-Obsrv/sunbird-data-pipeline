import async from "asyncawait/async";
import await from "asyncawait/await";
import HttpStatus from "http-status-codes";
import { IValidationResponse } from "../models/models";
import { ILimits } from "../models/models";
import { IQuery } from "../models/models";
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
        return async((request: IQuery, response: any, next: any) => {
            const result: IValidationResponse = ValidationService.validate(request, this.limits);
            if (result.status) { next(); } else { response.status(HttpStatus.BAD_REQUEST).send(result).end(); }
        });
    }

    public fetch() {
        return async(async (query: IQuery) => {
            try {
                const result = await this.httpService.fetch(query);
                return result;
            } catch (error) {
                throw new Error("Unable to handle the query" + error);
            }
        });
    }
}
