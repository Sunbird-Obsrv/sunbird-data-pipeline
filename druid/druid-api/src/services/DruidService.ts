import async from "asyncawait/async";
import await from "asyncawait/await";
import HttpStatus from "http-status-codes";
import { IValidationResponse } from "../models/models";
import { ILimits } from "../models/models";
import { IQuery } from "../models/models";
import { APILogger } from "./ApiLogger";
import { HttpService } from "./HttpService";
import { ValidationService } from "./ValidationService";

/**
 * DruidService which facilitate user query to filter and validate.
 */

export class DruidService {
    private limits: ILimits;
    private httpService: HttpService;
    constructor(limits: ILimits, httpService: HttpService) {
        this.limits = limits;
        this.httpService = httpService;
    }
    /**
     * Which acts as a proxy api Middleware to validate/filter the user query.
     */
    public validate() {
        return async((query: IQuery, response: any, next: any) => {
            APILogger.log("User query is " + JSON.stringify(query));
            const result: IValidationResponse = ValidationService.validate(query, this.limits);
            if (result.isValid) { next(); } else { response.status(HttpStatus.BAD_REQUEST).send(result).end(); }
        });
    }

    /**
     * Which is used to fetch the result from the result from the external system.
     */
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
