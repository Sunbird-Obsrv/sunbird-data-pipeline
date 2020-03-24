import async from "asyncawait/async";
import await from "asyncawait/await";
import { AxiosRequestConfig } from "axios";
import HttpStatus from "http-status-codes";
import _ from "lodash";
import { IValidationResponse } from "../models/models";
import { IDataSourceLimits, IQuery } from "../models/models";
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
        return async((requestObj: any, response: any, next: any, ...pathsToSkip: string[]) => {
            const skipValidation = pathsToSkip.some((path) => path === requestObj.path);
            if (skipValidation) {
                next();
            } else {
                const query: IQuery = requestObj.body;
                const result: IValidationResponse = ValidationService.validate(query, this.getLimits(query.dataSource));
                if (result.isValid) { next(); } else { response.status(HttpStatus.FORBIDDEN).send(result).end(); }
            }
        });
    }

    /**
     * Which is used to fetch the result from the result from the external system.
     */
    public fetch() {
        return async(async (api: string, method: AxiosRequestConfig["method"], query?: IQuery) => {
            try {
                const result = await this.httpService.fetch(api, method, query);
                return result;
            } catch (error) {
                APILogger.log(`Failed to fetch the result ${error}`);
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
