package org.ekstep.ep.samza.service;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.exceptions.DriverException;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Aggregate;
import org.ekstep.ep.samza.domain.BatchEvent;
import org.ekstep.ep.samza.domain.QuestionData;
import org.ekstep.ep.samza.task.AssessmentAggregatorConfig;
import org.ekstep.ep.samza.task.AssessmentAggregatorSink;
import org.ekstep.ep.samza.task.AssessmentAggregatorSource;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.ContentCache;
import org.ekstep.ep.samza.util.ContentUtil;
import org.ekstep.ep.samza.util.DBUtil;
import org.ekstep.ep.samza.util.RestUtil;
import org.joda.time.DateTime;

import java.util.*;

public class AssessmentAggregatorService {

    private static Logger LOGGER = new Logger(AssessmentAggregatorService.class);
    private DBUtil dbUtil;
    private ContentUtil  contentUtil;
    private Comparator<QuestionData> byEts = (QuestionData o1, QuestionData o2) -> Long.compare(o2.getEts(), o1.getEts());

    public AssessmentAggregatorService(CassandraConnect cassandraConnect, AssessmentAggregatorConfig config, ContentCache contentCache, RestUtil restUtil) {
        this.dbUtil = new DBUtil(cassandraConnect, config);
        this.contentUtil = new ContentUtil(config, contentCache, restUtil);
    }

    public void process(AssessmentAggregatorSource source, AssessmentAggregatorSink sink) throws Exception {
        try {
            BatchEvent batchEvent = source.getEvent();
            if (!validateEvent(batchEvent)) {
                LOGGER.info(batchEvent.attemptId(), ": Batch Event validation failed. | ContentId : " + batchEvent.contentId() + " does not belong to CourseId :" + batchEvent.courseId() + ", skipping");
                sink.skip(batchEvent);
                return;
            }
            Row assessment = dbUtil.getAssessmentFromDB(batchEvent);
            if (null != assessment) {
            	sink.incDBHits();
            }
            if (null != assessment) {
                Long last_attempted_on = assessment.getTimestamp("last_attempted_on").getTime();
                if (batchEvent.assessmentEts() > last_attempted_on) {
                	saveAssessment(assessment, batchEvent);
                	sink.incDBUpdateCount();
                	sink.batchSuccess();
                } else {
                	LOGGER.info(batchEvent.attemptId(), ": Batch Event older than last assessment time, skipping");
                    sink.skip(batchEvent);
                }
            } else {
            	saveAssessment(assessment, batchEvent);
            	sink.incDBInsertCount();
            	sink.batchSuccess();
            }
            
        } catch (DriverException ex) {
        	ex.printStackTrace();
            LOGGER.error("", "Exception while fetching from db : " + ex);
            throw new DriverException(ex);
        } catch (Exception ex) {
        	ex.printStackTrace();
            LOGGER.error("", "Failed to parse the batchEvent: ", ex);
            sink.fail(source.getMessage().toString());
        }
    }

    public void saveAssessment(Row assessment, BatchEvent batchEvent) {
    	Long createdOn = null != assessment ? assessment.getTimestamp("created_on").getTime() : new DateTime().getMillis();
        Aggregate assess = getAggregateData(batchEvent, createdOn);
        dbUtil.updateAssessmentToDB(batchEvent, assess, createdOn);
        LOGGER.info("", " Successfully Aggregated the batch event - batchid: " + batchEvent.batchId()
                + " ,userid: " + batchEvent.userId() + " ,couserid: " + batchEvent.courseId()
                + " ,contentid: " + batchEvent.contentId());
    }

    public Aggregate getAggregateData(BatchEvent batchEvent, Long createdOn) {
        double totalMaxScore = 0;
        double totalScore = 0;
        List<UDTValue> questionsList = new ArrayList<>();
        TreeSet<QuestionData> questionSet = new TreeSet<QuestionData>(byEts);
        HashMap<String, String> checkDuplicate = new HashMap<String, String>();
        for (Map<String, Object> event : batchEvent.assessEvents()) {
            if (event.containsKey("edata")) {
                QuestionData questionData = new Gson().fromJson(new Gson().toJson(event.get("edata")), QuestionData.class);
                questionData.setEts(((Number) event.get("ets")).longValue());
                questionSet.add(questionData);
            }
        }
        for (QuestionData questionData : questionSet) {
            if (!checkDuplicate.containsKey(questionData.getItem().getId())) {
                totalScore += questionData.getScore();
                totalMaxScore += questionData.getItem().getMaxScore();
                questionsList.add(dbUtil.getQuestion(questionData));
                checkDuplicate.put(questionData.getItem().getId(), "");
            }
        }
        return new Aggregate(totalScore, totalMaxScore, questionsList);
    }

    private Boolean validateEvent(BatchEvent event) throws Exception {
        String courseId = event.courseId();
        String contentId = event.contentId();
        if (StringUtils.isNotBlank(courseId) && StringUtils.isNotBlank(contentId)) {
            return contentUtil.getLeafNodes(courseId).contains(contentId) ? true : false;
        }
        return false;
    }


    
}
