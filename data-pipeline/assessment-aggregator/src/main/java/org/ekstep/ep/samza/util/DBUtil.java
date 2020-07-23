package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.ekstep.ep.samza.domain.Aggregate;
import org.ekstep.ep.samza.domain.BatchEvent;
import org.ekstep.ep.samza.domain.QuestionData;
import org.ekstep.ep.samza.task.AssessmentAggregatorConfig;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class DBUtil {
    private CassandraConnect cassandraConnect;
    private AssessmentAggregatorConfig config;
    private UserType questionType;

    public DBUtil(CassandraConnect cassandraConnect, AssessmentAggregatorConfig config) {
        this.cassandraConnect = cassandraConnect;
        this.config = config;
        questionType = cassandraConnect.getUDTType(config.getCoursesKeyspace(), config.getAssessmentQuestionUDT());
    }

    public UDTValue getQuestion(QuestionData questionData) {
        return questionType.newValue().setString("id", questionData.getItem().getId())
                .setDouble("max_score", questionData.getItem().getMaxScore())
                .setDouble("score", questionData.getScore())
                .setString("type", questionData.getItem().getType())
                .setString("title", questionData.getItem().getTitle())
                .setList("resvalues", questionData.getResvalues())
                .setList("params", questionData.getItem().getParams())
                .setString("description", questionData.getItem().getDesc())
                .setDecimal("duration", BigDecimal.valueOf(questionData.getDuration()))
                .setTimestamp("assess_ts", new Timestamp(questionData.getEts()));

    }

    public void updateAssessmentToDB(BatchEvent batchEvent, Aggregate aggregate, Long createdOn) {

        Insert query = QueryBuilder.insertInto(config.getCoursesKeyspace(), config.getAssessementTable())
                .value("course_id", batchEvent.courseId())
                .value("batch_id", batchEvent.batchId()).value("user_id", batchEvent.userId())
                .value("content_id", batchEvent.contentId()).value("attempt_id", batchEvent.attemptId())
                .value("updated_on", new DateTime().getMillis()).value("created_on", createdOn)
                .value("last_attempted_on", batchEvent.assessmentEts()).value("total_score", aggregate.getTotalScore())
                .value("total_max_score", aggregate.getTotalMaxScore()).value("question", aggregate.getQuestionsList())
                .value("grand_total", aggregate.getGrandTotal());

        cassandraConnect.upsert(query);
    }


    public Row getAssessmentFromDB(BatchEvent event) {
        String query = QueryBuilder.select("last_attempted_on", "created_on").from(config.getCoursesKeyspace(), config.getAssessementTable())
                .where(QueryBuilder.eq("attempt_id", event.attemptId())).and(QueryBuilder.eq("batch_id", event.batchId()))
                .and(QueryBuilder.eq("user_id", event.userId())).and(QueryBuilder.eq("content_id", event.contentId()))
                .and(QueryBuilder.eq("course_id", event.courseId())).toString();
        return cassandraConnect.findOne(query);
    }
}
