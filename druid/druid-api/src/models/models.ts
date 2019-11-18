export interface IValidationResponse {
    "error"?: any;
    "errorMessage"?: string;
    "status": boolean;
}

export interface ILimits {
    cardinalColumns: string[];
    common: ICommon;
    queryRules: IQueryRules;
}

export interface ICommon {
    max_dimensions: number;
    max_result_limit: number;
}

export interface IQueryRules {
    groupBy: IGroupBy;
    scan: IScan;
    topN: ITopN;
}

export interface IGroupBy {
    max_date_range: number;
    max_filter_dimensions: number;
}

export interface IScan {
    max_date_range: number;
    max_filter_dimensions: number;
}

export interface ITopN {
    max_date_range: number;
    max_filter_dimensions: number;
}

export interface IQuery {
    queryType: string;
    dataSource: string;
    dimensions: string[];
    filter: any;
    aggregations: any[];
    postAggregations: any[];
    limit: number;
}
