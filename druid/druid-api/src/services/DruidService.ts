import async from "asyncawait/async";
import await from "asyncawait/await";
import { AxiosRequestConfig } from "axios";
import HttpStatus from "http-status-codes";
import _ from "lodash";
import { IValidationResponse } from "../models/models";
import { IDataSourceLimits, ILimits, IQuery } from "../models/models";
import { APILogger } from "./ApiLogger";
import { HttpService } from "./HttpService";
import { ValidationService } from "./ValidationService";

/**
 * DruidService which facilitate user query to filter and validate.
 */

export class DruidService {
    private httpService: HttpService;
    private dataSourceLimits: IDataSourceLimits;
    constructor(dataSourceLimits: IDataSourceLimits, httpService: HttpService) {
        this.dataSourceLimits = dataSourceLimits;
        this.httpService = httpService;
    }
    /**
     * Which acts as a proxy api Middleware to validate/filter the user query.
     */
    public validate() {
        return async((query: IQuery, response: any, next: any) => {
            APILogger.log("User query is " + JSON.stringify(query));
            const result: IValidationResponse = ValidationService.validate(query, this.getLimits(query.dataSource));
            // tslint:disable-next-line: max-line-length
            if (result.isValid) { next(); } else { response.status(HttpStatus.INTERNAL_SERVER_ERROR).send(result).end(); }
        });
    }

    /**
     * Which validates the api auth key which is present in the headers
     */
    public validateKey() {
        return async((key: string = "", response: any, next: any) => {
            const result: IValidationResponse = ValidationService.isValidKey(key);
            if (result.isValid) { next(); } else { response.status(HttpStatus.UNAUTHORIZED).send(result).end(); }
        });
    }

    /**
     * Which is used to fetch the result from the result from the external system.
     */
    public fetch() {
        return async(async (api: string, method: AxiosRequestConfig["method"], query?: IQuery) => {
            try {
                const result = await HttpService.fetch(api, method, query);
                return result;
            } catch (error) {
                APILogger.log(`Failed to fetch the result ${error}`);
                console.log("error" + error);
                throw new Error("Unable to handle the query, Please try after some time.");
            }
        });
    }

    /**
     * Which returns the rules/limits for the particular dataSource.
     */
    public getLimits(dataSource: string): any {
        return _.find(this.dataSourceLimits.limits, ["dataSource", dataSource]);
    }
}
