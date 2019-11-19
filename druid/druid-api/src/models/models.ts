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
    groupBy: IRules;
    scan: IRules;
    topN: IRules;
    select: IRules;
    timeseries: IRules;
}

export interface IRules {
    max_date_range?: number;
    max_filter_dimensions?: number;
}

export interface IDimension { [name: string]: any; }

export interface IQuery {
    queryType: string;
    dataSource: string;
    dimension?: string;
    dimensions?: string[];
    filter?: IFilter;
    aggregations?: any[];
    postAggregations?: any[];
    limit?: number;
    intervals?: string[] | string;
}

export interface IFilter {
    type?: string;
    fields?: IFilter[];
    field?: IFilter;
    dimension?: string;
    dimensions?: string[];

}
