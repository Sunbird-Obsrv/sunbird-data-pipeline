package org.ekstep.ep.samza.service;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.UDTValue;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Aggregate;
import org.ekstep.ep.samza.domain.BatchEvent;
import org.ekstep.ep.samza.domain.QuestionData;
import org.ekstep.ep.samza.task.AssessmentAggregatorSink;
import org.ekstep.ep.samza.task.AssessmentAggregatorSource;
import org.ekstep.ep.samza.util.DBUtil;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssessmentAggregatorService {

    private static Logger LOGGER = new Logger(AssessmentAggregatorService.class);
    private DBUtil dbUtil;

    public AssessmentAggregatorService(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public void process(AssessmentAggregatorSource source, AssessmentAggregatorSink sink) {
        try {
            BatchEvent batchEvent = source.getEvent();
            Row assessment = dbUtil.getAssessmentFromDB(batchEvent);

            if (isBatchEventValid(batchEvent, assessment)) {
                Long createdOn = null != assessment ? assessment.getTimestamp("created_on").getTime() : new DateTime().getMillis();
                Aggregate assess = getAggregateData(batchEvent, createdOn);
                dbUtil.updateAssessmentToDB(batchEvent, assess.getTotalMaxScore(), assess.getTotalScore(),
                        assess.getQuestionsList(), createdOn);
                sink.success();

            } else {
                LOGGER.info(batchEvent.attemptId(), ": Batch Event older than last assessment time, skipping");
                sink.skip();
            }
        } catch (JsonSyntaxException ex) {
            LOGGER.error("", "Failed to parse the batchEvent: ", ex);
            sink.failed();
        }
    }

    public Aggregate getAggregateData(BatchEvent batchEvent, Long createdOn) {
        int totalMaxScore = 0;
        int totalScore = 0;
        List<UDTValue> questionsList = new ArrayList<>();

        for (Map<String, Object> event : batchEvent.assessEvents()) {
            if (event.containsKey("edata")) {
                QuestionData questionData = new Gson().fromJson(new Gson().toJson(event.get("edata")), QuestionData.class);
                totalScore += questionData.getScore();
                totalMaxScore += questionData.getItem().getMaxScore();
                questionsList.add(dbUtil.getQuestion(questionData));
            }
        }
        return new Aggregate(totalScore, totalMaxScore, questionsList);
    }

    public boolean isBatchEventValid(BatchEvent event, Row assessment) {
        boolean status = false;
        if (null != assessment) {
            Long last_attempted_on = assessment.getTimestamp("last_attempted_on").getTime();

            if (event.assessmentets() > last_attempted_on) {
                status = true;
            }
        } else {
            status = true;
        }
        return status;
    }


}
