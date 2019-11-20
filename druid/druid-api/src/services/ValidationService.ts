import _ from "lodash";
import { ICommon, IFilter, ILimits, IQuery, IRules, IValidationResponse } from "../models/models";

export class ValidationService {
    public static validate(request: IQuery, limits: ILimits): IValidationResponse {
        // If the limit is exceeded than than the config then set to default.
        const commonRulesValidationStatus: IValidationResponse = this.validateCommonRules(request, limits.common);
        if (commonRulesValidationStatus.status) {
            console.log(request);
            switch (request.queryType.toLowerCase()) {
                case "groupby": return this.validateQueryTypes(request, limits.cardinalColumns,
                    limits.queryRules.groupBy);
                case "topn": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.topN);
                case "scan": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.scan);
                // tslint:disable-next-line:max-line-length
                case "select": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.select);
                // tslint:disable-next-line:max-line-length
                case "search": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.search);
                // tslint:disable-next-line:max-line-length
                case "timeseries": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.timeseries);
                default: return {
                    error: undefined,
                    errorMessage: `Unsupported query type"${request.queryType}"`,
                    status: false,
                };
            }
        } else {
            return commonRulesValidationStatus;
        }
    }

    private static validateQueryTypes(
        request: IQuery,
        cardinalColumns: string[],
        queryRules: IRules = {}): IValidationResponse {
        const isValidDateRange = this.isValidDateRange(request.intervals, queryRules.max_date_range);
        if (isValidDateRange.status) {
            // tslint:disable-next-line:max-line-length
            return this.validateCardinalColumns(request, cardinalColumns, queryRules.max_filter_dimensions, "filter");
        } else {
            return isValidDateRange;
        }

    }

    // tslint:disable-next-line:max-line-length
    private static validateCardinalColumns(query: IQuery, dimension: string[], maxDimensions: number = 0, where: string): IValidationResponse {
        let cardianalDimensionsCountIs = 0;
        if (maxDimensions) {
            if (where === "filter") {
                cardianalDimensionsCountIs = this.handleFilters(query.filter, dimension);
                console.log("CardinalDimsCountIs" + cardianalDimensionsCountIs);
            } else {
                cardianalDimensionsCountIs = dimension.length;
            }
            if (cardianalDimensionsCountIs > maxDimensions) {
                return {
                    error: undefined,
                    errorMessage: `CardinalColumns [Dimensions] in the "${where}" can not more than "${maxDimensions}"`,
                    status: false,
                };
            } else {
                return { status: true };
            }
        } else {
            return { status: true };

        }
    }
    // tslint:disable-next-line:max-line-length
    private static isValidDateRange(dateRange: string[] | string = "", allowedDateRangeIs: number = 0): IValidationResponse {
        if (allowedDateRangeIs && !_.isEmpty(dateRange)) {
            const date = Array.isArray(dateRange) ? dateRange[0].split("/") : dateRange.split("/");
            const fromDate = new Date(date[0]);
            const toDate = new Date(date[1]);
            // To calculate the time difference of two dates
            const differenceInTime = fromDate.getTime() - toDate.getTime();
            // To calculate the no. of days between two dates
            const differenceInDays = Math.abs(differenceInTime / (1000 * 3600 * 24));
            console.log("differenceInDays" + differenceInDays);
            if (fromDate > toDate) {
                return {
                    // tslint:disable-next-line:max-line-length
                    errorMessage: `Invalid date range, The end instant date must be greater than the start instant date`,
                    status: false,
                };
            } else if (differenceInDays > allowedDateRangeIs) {
                return {
                    errorMessage: `Date Range(intervals) can not be more than "${allowedDateRangeIs}" day's"`,
                    status: false,
                };
            } else {
                return { status: true };
            }
        } else {
            return { status: false, errorMessage: `Invalid date range, The date range is must` };
        }
    }

    private static validateCommonRules(request: IQuery, commonLimits: ICommon): IValidationResponse {
        console.log("threshold" + request.threshold);
        if (request.threshold) {
            request.threshold = request.threshold > commonLimits.max_result_threshold
                ? commonLimits.max_result_threshold : (request.threshold || commonLimits.max_result_threshold);
        } else {
            request.threshold = commonLimits.max_result_threshold;
        }
        // tslint:disable-next-line:max-line-length
        if (request.dimensions) { return this.validateCardinalColumns(request, request.dimensions, commonLimits.max_dimensions, ""); } else { return { status: true }; }
    }

    private static handleFilters(requestFilter: IFilter = {}, cardinalColumns: string[]): number {
        let cardianalDimensionsCountIs = 0;
        if (requestFilter.dimensions) {
            // tslint:disable-next-line:max-line-length
            cardianalDimensionsCountIs += this.getCardinalDimensionsCount(cardinalColumns, requestFilter.dimensions);
        }
        if (requestFilter.fields && requestFilter.fields.length) {
            const recursive = (filters: IFilter[]) => {
                _.forEach(filters, (key, value) => {
                    if (key.fields && key.fields.length) {
                        recursive(key.fields);
                    } else {
                        // tslint:disable-next-line:max-line-length
                        if (key.dimensions) { cardianalDimensionsCountIs += this.getCardinalDimensionsCount(cardinalColumns, key.dimensions); }
                        // tslint:disable-next-line:max-line-length
                        if (key.dimension) { cardianalDimensionsCountIs += this.getCardinalDimensionsCount(cardinalColumns, key.dimension.split(" ")); }
                    }
                });
            };
            recursive(requestFilter.fields);
        }
        return cardianalDimensionsCountIs;
    }

    // tslint:disable-next-line:max-line-length
    private static getCardinalDimensionsCount(cardinalColumns: string[] = [], dimensions: string[] = []): number {
        let count = 0;
        const result = _.countBy(dimensions);
        _.forEach(cardinalColumns, (dim, value) => {
            count += result[dim] || 0;
        });
        return count;
    }

}
