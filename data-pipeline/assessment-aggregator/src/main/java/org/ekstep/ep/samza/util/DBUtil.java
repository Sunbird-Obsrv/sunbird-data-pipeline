package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.ekstep.ep.samza.domain.BatchEvent;
import org.ekstep.ep.samza.domain.QuestionData;
import org.ekstep.ep.samza.task.AssessmentAggregatorConfig;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;


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
                .setInt("max_score", questionData.getItem().getMaxScore())
                .setInt("score", questionData.getScore())
                .setString("type", questionData.getItem().getType()).setString("title", questionData.getItem().getTitle())
                .setList("resvalues", questionData.getResvalues())
                .setList("params", questionData.getItem().getParams())
                .setString("description", questionData.getItem().getDesc())
                .setDecimal("duration", BigDecimal.valueOf(questionData.getDuration()));

    }

    public void updateAssessmentToDB(BatchEvent batchEvent, int totalMaxScore, int totalScore,
                                     List<UDTValue> questionsList, Long createdOn) {

        Insert query = QueryBuilder.insertInto(config.getCoursesKeyspace(), config.getAssessementTable())
                .value("course_id", batchEvent.courseId())
                .value("batch_id", batchEvent.batchId()).value("user_id", batchEvent.userId())
                .value("content_id", batchEvent.contentId()).value("attempt_id", batchEvent.attemptId())
                .value("updated_on", new DateTime().getMillis()).value("created_on", createdOn)
                .value("last_attempted_on", batchEvent.assessmentets()).value("total_score", totalScore)
                .value("total_max_score", totalMaxScore).value("question", questionsList);

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
