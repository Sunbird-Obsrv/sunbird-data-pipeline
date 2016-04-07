package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.model.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;


public class UserManagementTask implements StreamTask, InitableTask, ClosableTask {

    private String successTopic;
    private String failedTopic;
    private String dbUrl;
    private String dbUserName;
    private String dbPassword;
    private HikariDataSource dataSource;
    private Map<String, IModel> modelMap = new HashMap<String, IModel>();


    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopic = config.get("output.success.topic.name", "sandbox.learners");
        failedTopic = config.get("output.failed.topic.name", "sandbox.learners.fail");

        dbUrl = config.get("db.url");
        dbUserName = config.get("db.userName");
        dbPassword = config.get("db.password");

        initDataSource();
    }

    private void initDataSource() {
        String url = String.format("%s", dbUrl);
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(dbUserName);
        dataSource.setPassword(dbPassword);
        dataSource.setMaximumPoolSize(2);

        modelMap.put("GE_CREATE_PROFILE", new CreateProfileDto(dataSource));
        modelMap.put("GE_CREATE_USER", new CreateLearnerDto(dataSource));
        modelMap.put("GE_UPDATE_PROFILE", new UpdateProfileDto(dataSource));
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        try {
            Map<String, Object> jsonObject = (Map<String, Object>) envelope.getMessage();
            Event event = new Event(new Gson().toJson(jsonObject));
            processEvent(event, collector);
        } catch (Exception e) {
            System.err.println("Exception: " + e);
            e.printStackTrace(new PrintStream(System.err));
        }
    }

    public void processEvent(Event event, MessageCollector collector) throws Exception {
        IModel model = modelMap.get(event.getEId());
        if (model != null) {
            model.process(event);
            if (!model.getIsInserted()) {
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getMap()));
            }
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
        } else {
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
        }
    }

    @Override
    public void close() throws Exception {
        dataSource.close();
    }
}
