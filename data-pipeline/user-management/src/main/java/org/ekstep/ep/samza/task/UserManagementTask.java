package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.model.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;


public class UserManagementTask implements StreamTask, InitableTask, ClosableTask, WindowableTask {
    static Logger LOGGER = new Logger(UserManagementTask.class);

    private String successTopic;
    private String failedTopic;
    private String dbUrl;
    private String dbUserName;
    private String dbPassword;
    private HikariDataSource dataSource;
    private Map<String, IModel> modelMap = new HashMap<String, IModel>();
    private Counter messageCount;


    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopic = config.get("output.success.topic.name", "sandbox.learners");
        failedTopic = config.get("output.failed.topic.name", "sandbox.learners.fail");

        dbUrl = config.get("db.url");
        dbUserName = config.get("db.userName");
        dbPassword = config.get("db.password");

        initDataSource();
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
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
        Event event = null;
        try {
            Map<String, Object> jsonObject = (Map<String, Object>) envelope.getMessage();
            event = new Event(new Gson().toJson(jsonObject));
            processEvent(event, collector);
            messageCount.inc();
        } catch (Exception e) {
            LOGGER.error(null, "Exception, event: " + event, e);
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

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();

    }
}
