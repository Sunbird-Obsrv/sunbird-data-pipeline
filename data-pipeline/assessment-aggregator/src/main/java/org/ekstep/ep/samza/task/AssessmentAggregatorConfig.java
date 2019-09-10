package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class AssessmentAggregatorConfig {

    private final String JOB_NAME = "AssessmentAggregator";
    private final String metricsTopic;
    private final String coursesKeyspace;
    private final String assessementTable;
    private final String assessmentQuestionUDT;

    public AssessmentAggregatorConfig(Config config) {
        metricsTopic = config.get("output.metrics.topic.name", "pipeline_metrics");
        coursesKeyspace = config.get("middleware.cassandra.courses_keyspace", "sunbird_courses");
        assessementTable = config.get("middleware.cassandra.aggregator_table", "assessment_aggregator");
        assessmentQuestionUDT = config.get("middleware.cassandra.question_type", "question");

    }

    public String jobName() {
        return JOB_NAME;
    }

    public String metricsTopic() {
        return metricsTopic;
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

}
