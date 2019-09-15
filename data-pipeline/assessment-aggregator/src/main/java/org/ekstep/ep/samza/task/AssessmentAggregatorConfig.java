package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class AssessmentAggregatorConfig {

    private final String JOB_NAME = "AssessmentAggregator";
    private final String metricsTopic;
    private String failedTopic;
    private final String coursesKeyspace;
    private final String assessementTable;
    private final String assessmentQuestionUDT;
    private final String cassandraHost;
    private final int cassandraPort;

    public AssessmentAggregatorConfig(Config config) {
        metricsTopic = config.get("output.metrics.topic.name", "pipeline_metrics");
        coursesKeyspace = config.get("middleware.cassandra.courses_keyspace", "sunbird_courses");
        assessementTable = config.get("middleware.cassandra.aggregator_table", "assessment_aggregator");
        assessmentQuestionUDT = config.get("middleware.cassandra.question_type", "question");
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        cassandraHost = config.get("middleware.cassandra.host", "127.0.0.1");
        cassandraPort = config.getInt("middleware.cassandra.port", 9042);

    }

    public String jobName() {
        return JOB_NAME;
    }

    public String metricsTopic() {
        return metricsTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String getCoursesKeyspace() {
        return coursesKeyspace;
    }

    public String getAssessementTable() {
        return assessementTable;
    }

    public String getAssessmentQuestionUDT() {
        return assessmentQuestionUDT;
    }

    public String getCassandraHost() { return cassandraHost; }

    public int getCassandraPort() { return  cassandraPort; }

}
