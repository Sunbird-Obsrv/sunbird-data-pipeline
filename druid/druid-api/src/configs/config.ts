export const config = {
    apiEndPoint: process.env.druid_proxy_api_endPoint || "/druid/v2",
    apiPort: process.env.druid_proxy_api_port || 8082,
    druidDataSourceEndPoint: process.env.druid_datasource_endPoint || "/druid/v2/datasources",
    druidEndPoint: process.env.druid_proxy_api_endPoint || "/druid/v2",
    druidHost: process.env.druid_host || "http://11.2.1.20",
    druidPort: process.env.druid_port || 8082,
    druidSqlEndPoint: process.env.druid_sql_endPoint || "/druid/v2/sql",
    druidWhiteListedKeys: process.env.druid_cql_whitlisted_keys || ["2QyHp4q35lL9XzI3i5f1FVSYmtWeGvq2", "7rFRS7gycgpnbccA6GucQlAvq8a6aDkB"],
    limits: [{ // Keeping limits per data source.
        cardinalColumns: [ // High cardinal dimensions
            "context_sid", // Telemetry, context session id
            "context_did", // Telemetry, context did
            "actor_id", // User id
            "object_id", // Content Id
            "syncts", // Sync Time Stamp
            "mid", // Uniq id
            "device_id",
        ],
        common: {
            max_dimensions: 10, // Maximum number of high cardinal dimensions are allowed.
            max_result_threshold: 1000, // Allowed max result is 1000.
        },
        dataSource: "telemetry-events", // Name of the data source.
        queryRules: {
            groupBy: {
                max_date_range: 30,
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            scan: {  // Query Type
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed dimensions
            },
            search: {  // Query Type
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed dimensions
            },
            segmentMetadata: {
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            select: {
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            timeBoundary: {
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            timeseries: {
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            topN: {
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
        },
    },
    {
        cardinalColumns: [ // High cardinal dimensions
            "dimensions_did", // Summary, dimension device id
            "dimensions_sid", // Summary, dimension session id
            "actor_id", // User id
            "object_id", // Content Id
            "syncts", // Sync Time Stamp
            "mid", // Uniq id
            "device_id",
        ],
        common: {
            max_dimensions: 10, // Maximum number of high cardinal dimensions are allowed.
            max_result_threshold: 1000, // Allowed max result is 1000.
        },
        dataSource: "summary-events", // Name of the data source
        queryRules: {
            groupBy: {
                max_date_range: 30,
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            scan: {  // Query Type
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed dimensions
            },
            search: {  // Query Type
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed dimensions
            },
            segmentMetadata: {
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            select: {
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            timeBoundary: {
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            timeseries: {
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            topN: {
                max_date_range: 30, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
        },
    }],
    log: {
        backups: 5,
        logFilePath: "logs/druid-proxy-api.log",
        maxLogSize: 10485760,
        pattern: "yyyy-MM-dd-hh",
    },
};
