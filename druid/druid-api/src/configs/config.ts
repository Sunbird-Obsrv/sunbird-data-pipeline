export const config = {
    apiEndPoint: "/druid/v2/",
    apiPort: 8082,
    druidEndPoint: "/druid/v2/",
    druidHost: "http://11.2.1.20",
    druidPort: 8082,
    limits: {
        cardinalColumns: [ // High cardinal dimensions
            "context_sid", // Telemetry, context session id
            "dimensions_did", // Summary, dimension device id
            "dimensions_sid", // Summary, dimension session id
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
            select: {
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
    log: {
        backups: 5,
        logFilePath: "logs/druid-proxy-api.log",
        maxLogSize: 10485760,
        pattern: "yyyy-MM-dd-hh",
    },
};
