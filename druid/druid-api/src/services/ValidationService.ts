import _ from "lodash";
import { ICommon, IFilter, ILimits, IQuery, IRules, IValidationResponse } from "../models/models";

export class ValidationService {
    public static validate(request: IQuery, limits: ILimits): IValidationResponse {
        // If the limit is exceeded than than the config then set to default.
        const commonRulesValidationStatus: IValidationResponse = this.validateCommonRules(request, limits.common);
        if (commonRulesValidationStatus.status) {
            console.log(request.queryType);
            switch (request.queryType.toLowerCase()) {
                case "groupby": return this.validateQueryTypes(request, limits.cardinalColumns,
                    limits.queryRules.groupBy);
                case "topn": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.topN);
                case "scan": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.scan);
                case "groupby": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.scan);
                // tslint:disable-next-line:max-line-length
                case "select": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.select);
                // tslint:disable-next-line:max-line-length
                case "search": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.select);
                // tslint:disable-next-line:max-line-length
                case "timeseries": return this.validateQueryTypes(request, limits.cardinalColumns, limits.queryRules.timeseries);
                default: return {
                    error: undefined,
                    errorMessage: `Dimensions can not be more than "${limits.common.max_dimensions}"`,
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
        // tslint:disable-next-line:max-line-length

        if (isValidDateRange) {
            // tslint:disable-next-line:max-line-length
            return this.validateCardinalColumns(request, cardinalColumns, queryRules.max_filter_dimensions);
        } else {
            return {
                error: undefined,
                errorMessage: `Date Range(intervals) can not be more than "${queryRules.max_date_range}" for "${request.queryType}"`,
                status: false,
            };
        }

    }

    // tslint:disable-next-line:max-line-length
    private static validateCardinalColumns(query: IQuery, dimension: string[], maxDimensions: number = 0): IValidationResponse {
        if (maxDimensions) {
            const cardianalDimensionsCountIs: number = this.handleFilters(query.filter, dimension);
            if (cardianalDimensionsCountIs > maxDimensions) {
                return {
                    error: undefined, errorMessage: `CardinalColumns can not more than "${maxDimensions}"`,
                    status: false,
                };
            } else {
                return { status: true };
            }
        } else {
            return { status: true };

        }
    }
    private static isValidDateRange(dateRange: string[] | string = "", allowedDateRangeIs: number = 0): boolean {
        if (allowedDateRangeIs && !_.isEmpty(dateRange)) {
            const date = Array.isArray(dateRange) ? dateRange[0].split("/") : dateRange.split("/");
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

    private static validateCommonRules(request: IQuery, commonLimits: ICommon): IValidationResponse {
        if (request.limit) {
            request.limit = request.limit > commonLimits.max_result_limit
                ? commonLimits.max_result_limit : (request.limit || commonLimits.max_result_limit);
        } else {
            request.limit = commonLimits.max_result_limit;
        }
        if (request.dimensions && request.dimensions.length > commonLimits.max_dimensions) {
            return {
                error: undefined,
                errorMessage: `Dimensions can not be more than "${commonLimits.max_dimensions}"`,
                status: false,
            };
        } else {
            return {
                status: true,
            };
        }
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