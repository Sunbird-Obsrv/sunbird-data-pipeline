package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.mozilla.universalchardet.UniversalDetector;

import java.sql.*;
import java.util.UUID;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;


public class UserManagementTestTask implements StreamTask, InitableTask, ClosableTask {

    private String successTopic;
    private String failedTopic;
    private String dbUrl;
    private String dbUserName;
    private String dbPassword;
    private HikariDataSource dataSource;

    private static String uid;
    private static String handle;
    private static String gender;
    private static Integer yearOfBirth;
    private static Integer age;
    private static Integer standard;
    private static String language;
    private static Timestamp createdAt;
    private static Timestamp updatedAt;


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
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        try {
            Map<String, Object> jsonObject = (Map<String, Object>) envelope.getMessage();
            Map<String, Object> edata = (Map<String, Object>) jsonObject.get("edata");
            Map<String, Object> eks = (Map<String, Object>) edata.get("eks");
            String _handle = String.valueOf(eks.get("handle"));
            processEvent(_handle);
        } catch (Exception e) {
            System.err.println("Exception: " + e);
            e.printStackTrace(new PrintStream(System.err));
        }
    }

    public void processEvent(String _handle) throws Exception {

        uid=UUID.randomUUID().toString();
        handle="गुड्डी";
        gender="female";
        yearOfBirth=2015;
        age=5;
        standard=5;
        language="ML";
        java.util.Date date = new java.util.Date();
        createdAt = new Timestamp(date.getTime());
        updatedAt = new Timestamp(date.getTime());


        PreparedStatement preparedStmt = null;
        Connection connection = null;

        String encoding = getCharset(handle);

        if (encoding != null) {
            System.out.println("Detected encoding = " + encoding);
        } else {
            System.out.println("No encoding detected.");
        }

        encoding = getCharset(_handle);

        if (encoding != null) {
            System.out.println("[event] Detected encoding = " + encoding);
        } else {
            System.out.println("[event] No encoding detected.");
        }

        try {
            connection = dataSource.getConnection();
            String query = " insert into profile (uid, handle, year_of_birth, gender, age, standard, language, created_at, updated_at)"
                    + " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString(1, uid);
            preparedStmt.setString(2, handle);

            preparedStmt.setInt(3, yearOfBirth);
            preparedStmt.setString(4, gender);
            preparedStmt.setInt(5, age);
            preparedStmt.setInt(6, standard);
            preparedStmt.setString(7, language);
            preparedStmt.setTimestamp(8, createdAt);
            preparedStmt.setTimestamp(9, updatedAt);

            int affectedRows = preparedStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating Profile failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                System.out.println("Inserted uid"+uid);
            }
            else {
                throw new SQLException("Creating Profile failed, no ID obtained.");
            }

        } catch (Exception e) {
            System.err.println("Exception: " + e);
            e.printStackTrace(new PrintStream(System.err));
        } finally {
            if(preparedStmt!=null)
                preparedStmt.close();
            if(connection!=null)
                connection.close();
        }
//
//        IModel model = modelMap.get(event.getEId());
//        if (model != null) {
//            model.process(event);
//            if (model.getIsInserted()) {
//                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
//            } else {
//                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getMap()));
//            }
//        } else {
//            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
//        }
    }

    private String getCharset(String string) {
        UniversalDetector detector = new UniversalDetector(null);
        byte[] bytes = string.getBytes();
        detector.handleData(bytes,0,bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        return encoding;
    }

    @Override
    public void close() throws Exception {
        dataSource.close();
    }
}
