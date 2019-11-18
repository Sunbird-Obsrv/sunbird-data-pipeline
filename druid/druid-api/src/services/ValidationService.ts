import { ILimits, IQuery, IRules, IValidationResponse } from "../models/models";
export class ValidationService {
    public static validate(request: IQuery, limits: ILimits): IValidationResponse {
        // If the limit is exceeded than than the config then set to default.
        request.limit = request.limit || limits.common.max_result_limit;
        // Check dimensions size is more than the defined di mention in the config.
        console.log("req" + request.dimensions);
        if (request.dimensions && request.dimensions.length > limits.common.max_dimensions) {
            console.log("yes...inside");
            return {
                error: undefined,
                errorMessage: `Dimensions can not be more than "${limits.common.max_dimensions}"`,
                status: false,
            };
        } else {
            console.log(request.queryType);
            switch (request.queryType.toLowerCase()) {
                case "groupby": return this.validateQueryTypes(request, limits.cardinalColumns,
                    limits.queryRules.groupBy);
                case "topn": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.topN);
                case "scan": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.scan);
                case "groupby": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.scan);
                // tslint:disable-next-line:max-line-length
                case "select": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.select);
                default: return {
                    error: undefined,
                    errorMessage: `Dimensions can not be more than "${limits.common.max_dimensions}"`,
                    status: false,
                };
            }
        }
    }

    private static validateQueryTypes(
        request: IQuery,
        cardinalColumns: string[],
        queryRules: IRules): IValidationResponse {
        const isValidDateRange = this.isValidDateRange(request.intervals, queryRules.max_date_range);
        // tslint:disable-next-line:max-line-length
        const isValidCardinalColumns = this.validateCardinalColumns(request, cardinalColumns, queryRules.max_filter_dimensions);
        if (isValidDateRange) {
            if (isValidCardinalColumns) {
                return {
                    error: undefined, errorMessage: `CardinalColumns can not more than "${queryRules.max_filter_dimensions}"`,
                    status: false,
                };
            } else {
                return {
                    error: undefined,
                    errorMessage: `Date Range(intervals) can not be more than "${queryRules.max_date_range}" for "${request.queryType}"`,
                    status: false,
                };
            }
        } else {
            return { status: true };
        }

    }

    private static validateCardinalColumns(query: IQuery, dimension: string[], maxDimensions: number = 0): boolean {
        if (maxDimensions) {
            return true;
        } else {
            return true;
        }
    }
    private static isValidDateRange(dateRange: string[] = [], allowedDateRangeIs: number = 0): boolean {
        if (allowedDateRangeIs && dateRange.length) {
            const date = dateRange[0].split("/");
            const fromDate = new Date(date[0]);
            const toDate = new Date(date[1]);
            // To calculate the time difference of two dates
            const differenceInTime = fromDate.getTime() - toDate.getTime();
            // To calculate the no. of days between two dates
            const differenceInDays = differenceInTime / (1000 * 3600 * 24);
            console.log("differenceInDays" + differenceInDays);
            return differenceInDays > allowedDateRangeIs ? false : true;
        } else {
            return false;
        }

    }
}
