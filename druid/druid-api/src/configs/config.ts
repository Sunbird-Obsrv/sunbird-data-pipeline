export const config = {
    endpoint: "/druid/v2/",
    host: "localhost",
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
        ],
        common: {
            max_dimensions: 10, // Maximum number of high cardinal dimensions are allowed.
            max_result_limit: 1000, // Allowed max result is 1000.
        },

        queryRules: {
            groupBy: {
                max_date_range: 7,
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            scan: {  // Query Type
                max_date_range: 7, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed dimensions
            },
            select: {
                max_date_range: 7, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            timeseries: {
                max_date_range: 7, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
            topN: {
                max_date_range: 7, // Maximum allowed date range, In days.
                max_filter_dimensions: 50, // Maximum allowed date range, In days.
            },
        },
    },
    port: 8082,

};
