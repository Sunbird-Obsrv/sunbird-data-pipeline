import httpStatus from "http-status";
import _ from "lodash";
import moment from "moment";
import { config } from "../configs/config";
import { ICommon, IFilter, ILimits, IQuery, IRules, IValidationResponse } from "../models/models";
import { APILogger } from "./ApiLogger";

/**
 * Service to validate/filter/limits the user druid queries.
 */
export class ValidationService {
    /**
     * Method to validate the user druid query for every queryType.
     * @param query IQuery - User druid query/request.
     * @param limits ILimits - Limit/filter Configurations to validate the queries.
     */
    public static validate(query: IQuery, limits: ILimits): IValidationResponse {
        if (!_.isUndefined(limits) || !_.isEmpty(limits)) {
            APILogger.warn(` Addling limits/rules for  ${limits.dataSource}`);
            // If the limit is exceeded than than the config then set to default.
            const commonRulesValidationStatus: IValidationResponse = this.validateCommonRules(query, limits.common);
            if (commonRulesValidationStatus.isValid) {
                switch (query.queryType.toLowerCase()) {
                    case "groupby": return this.validateQueryTypes(query, limits.cardinalColumns,
                        limits.queryRules.groupBy);
                    case "topn": return this.validateQueryTypes(query, limits.cardinalColumns, limits.queryRules.topN);
                    case "scan": return this.validateQueryTypes(query, limits.cardinalColumns, limits.queryRules.scan);
                    // tslint:disable-next-line:max-line-length
                    case "select": return this.validateQueryTypes(query, limits.cardinalColumns, limits.queryRules.select);
                    // tslint:disable-next-line:max-line-length
                    case "search": return this.validateQueryTypes(query, limits.cardinalColumns, limits.queryRules.search);
                    // tslint:disable-next-line:max-line-length
                    case "timeseries": return this.validateQueryTypes(query, limits.cardinalColumns, limits.queryRules.timeseries);
                    // tslint:disable-next-line:max-line-length
                    case "timeboundary": return this.validateQueryTypes(query, limits.cardinalColumns, limits.queryRules.timeBoundary);
                    default: return { isValid: true };
                }
            } else {
                return commonRulesValidationStatus;
            }
        } else {
            APILogger.warn(`Rules are not found, Hence allowing user to query ${query}`);
            return {
                isValid: true,
            };
        }
    }

    /**
     * Private method, Being called by validate method.
     * @param query IQuery - User druid query/request.
     * @param cardinalColumns string[] - List of high cardinal columns which defined in the api config.
     * @param queryRules IRules - Query rules which is defined in the api config for each query type.
     */
    private static validateQueryTypes(
        query: IQuery,
        cardinalColumns: string[],
        queryRules: IRules = {}): IValidationResponse {
        const dateRange = this.isValidDateRange(query.intervals, queryRules.max_date_range);
        if (dateRange.isValid) {
            // tslint:disable-next-line:max-line-length
            return this.validateCardinalColumns(query, cardinalColumns, queryRules.max_filter_dimensions, "filter");
        } else {
            return dateRange;
        }

    }

    /**
     * Private method, Being called by validateQueryTypes method.
     * @param query IQuery - User druid query/request.
     * @param dimension string[] - List of high cardinal columns which defined in the api config.
     * @param maxDimensions number - Allowed max dimension, which is defined in the api config.
     * @param where string - In the query dimensions may present inside the filter or it can be in the flatten.
     */
    // tslint:disable-next-line:max-line-length
    private static validateCardinalColumns(query: IQuery, dimension: string[], maxDimensions: number = 0, where: string): IValidationResponse {
        let cardianalDimensionsCountIs = 0;
        if (maxDimensions) {
            if (where === "filter") {
                cardianalDimensionsCountIs = this.handleFilters(query.filter, dimension);
            } else {
                cardianalDimensionsCountIs = dimension.length;
            }
            if (cardianalDimensionsCountIs > maxDimensions) {
                return {
                    error: httpStatus["403_NAME"],
                    errorMessage: `CardinalColumns [Dimensions] in the "${where}" can not more than "${maxDimensions}"`,
                    isValid: false,
                };
            } else {
                return { isValid: true };
            }
        } else {
            return { isValid: true };

        }
    }
    /**
     * Method to validate the date range. If the date range is higher than the limit.
     * Then api should reject the query.
     * @param dateRange string[] | string - Intervals.
     * @param allowedDateRangeIs number - allowed max date range which is defined in the api config.
     */
    // tslint:disable-next-line:max-line-length
    private static isValidDateRange(dateRange: string[] | string = "", allowedDateRangeIs: number = 0): IValidationResponse {
        if (allowedDateRangeIs && !_.isEmpty(dateRange)) {
            const date = Array.isArray(dateRange) ? dateRange[0].split("/") : dateRange.split("/");
            const fromDate = moment(date[0]);
            const toDate = moment(date[1]);
            // To calculate the time difference of two dates
            const differenceInDays = Math.abs(fromDate.diff(toDate, "days"));
            // To calculate the no. of days between two dates
            APILogger.log("DATE Difference is: " + differenceInDays);
            if (fromDate > toDate) {
                return {
                    // tslint:disable-next-line:max-line-length
                    error: httpStatus["403_NAME"],
                    // tslint:disable-next-line: max-line-length
                    errorMessage: `Invalid date range, The end instant date must be greater than the start instant date`,
                    isValid: false,
                };
            } else if (differenceInDays > allowedDateRangeIs) {
                return {
                    error: httpStatus["403_NAME"],
                    errorMessage: `Date Range(intervals) can not be more than "${allowedDateRangeIs}" day's"`,
                    isValid: false,
                };
            } else {
                return { isValid: true };
            }
        } else {
            return { error: httpStatus["403_NAME"], isValid: false, errorMessage: `Invalid date range, The date range is must` };
        }
    }

    /**
     * Method to validate the generic rules which is defined in the api config.
     * @param query IQuery - User druid query
     * @param commonLimits ICommon - Generic config which is defined in the api config.
     */
    private static validateCommonRules(query: IQuery, commonLimits: ICommon): IValidationResponse {
        if (query.threshold) {
            query.threshold = query.threshold > commonLimits.max_result_threshold
                ? commonLimits.max_result_threshold : (query.threshold || commonLimits.max_result_threshold);
        } else {
            query.threshold = commonLimits.max_result_threshold;
        }
        // tslint:disable-next-line:max-line-length
        if (query.dimensions) { return this.validateCardinalColumns(query, query.dimensions, commonLimits.max_dimensions, ""); } else { return { isValid: true }; }
    }

    /**
     * Method to validate the filters
     * @param queryFilter IFilter - User druid filter query.
     * @param cardinalColumns string[] - High Cardinal columns which is defined in the api config.
     */
    private static handleFilters(queryFilter: IFilter = {}, cardinalColumns: string[]): number {
        let cardianalDimensionsCountIs = 0;
        if (queryFilter.dimensions) {
            // tslint:disable-next-line:max-line-length
            cardianalDimensionsCountIs += this.getCardinalDimensionsCount(cardinalColumns, queryFilter.dimensions);
        }
        if (queryFilter.fields && queryFilter.fields.length) {
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
            recursive(queryFilter.fields);
        }
        APILogger.log("Total Cardinal Dims count is" + cardianalDimensionsCountIs);
        return cardianalDimensionsCountIs;
    }
    /**
     * Private method, Which is being called by handleFilter method, to get the count of
     * High cardinal columns
     * @param cardinalColumns string[] - High cardinal columns list, Which is defined in the api config.
     * @param dimensions string[] - dimensions which is present in the user druid query.
     */
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
