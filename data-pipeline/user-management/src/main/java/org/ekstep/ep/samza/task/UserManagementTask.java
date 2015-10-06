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
import java.util.ArrayList;
import java.util.Map;


public class UserManagementTask implements StreamTask, InitableTask, ClosableTask {

    private String successTopic;
    private String failedTopic;
    private String dbHost;
    private String dbPort;
    private String dbUserName;
    private String dbPassword;
    private String dbSchema;
    private HikariDataSource dataSource;
    private ArrayList<IModel> modelObjects = new ArrayList<IModel>();

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        successTopic = config.get("output.success.topic.name", "succeeded_user_management_events");
        failedTopic = config.get("output.failed.topic.name", "failed_user_management_events");

        dbHost = config.get("db.host");
        dbPort = config.get("db.port");
        dbUserName = config.get("db.userName");
        dbPassword = config.get("db.password");
        dbSchema = config.get("db.schema");

        initDataSource();
    }

    private void initDataSource() {
        String url = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbSchema);
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(dbUserName);
        dataSource.setPassword(dbPassword);

        modelObjects.add(new CreateProfileDto(dataSource));
        modelObjects.add(new CreateLearnerDto(dataSource));
        modelObjects.add(new UpdateProfileDto(dataSource));
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        try{
            Map<String,Object> jsonObject = (Map<String,Object>) envelope.getMessage();
            Event event = new Event(new Gson().toJson(jsonObject));
            processEvent(event,collector);
        }
        catch(Exception e){
            System.err.println("Exception: " + e);
            e.printStackTrace(new PrintStream(System.err));
        }
    }

    public void processEvent(Event event,MessageCollector collector) throws Exception{
        for(IModel obj : modelObjects){
            if(obj.canProcessEvent(event.getEId())){
                obj.process(event);

                if(obj.getIsInserted()){
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
                }
                else{
                    collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getMap()));
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        dataSource.close();
    }
}
