package org.ekstep.ep.samza.task;


import com.zaxxer.hikari.HikariDataSource;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.model.Event;
import org.ekstep.ep.samza.model.IModel;
import org.ekstep.ep.samza.model.CreateLearnerDto;
import org.ekstep.ep.samza.model.CreateProfileDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class UserManagementTaskTest {
    private HikariDataSource dataSource;
    private ArrayList<IModel> modelObjects = new ArrayList<IModel>();
    private Config configMock;
    private TaskContext contextMock;
    private MessageCollector collectorMock;
    private IncomingMessageEnvelope envelopMock;
    private TaskCoordinator coordinatorMock;

    private static final String SUCCESS_TOPIC = "SUCCESS_TOPIC";
    private static final String FAILURE_TOPIC = "FAILURE_TOPIC";

    @Before
    public void setup() {

        configMock = Mockito.mock(Config.class);
        contextMock = Mockito.mock(TaskContext.class);
        collectorMock = mock(MessageCollector.class);
        envelopMock = mock(IncomingMessageEnvelope.class);
        coordinatorMock = mock(TaskCoordinator.class);

        when(configMock.get("output.success.topic.name", "succeeded_user_management_events")).thenReturn(SUCCESS_TOPIC);
        when(configMock.get("output.failed.topic.name", "failed_user_management_events")).thenReturn(FAILURE_TOPIC);
        when(configMock.get("db.host")).thenReturn("localhost");
        when(configMock.get("db.port")).thenReturn("3306");
        when(configMock.get("db.userName")).thenReturn("jenkins");
        when(configMock.get("db.password")).thenReturn("ec0syst3m");
        when(configMock.get("db.schema")).thenReturn("eptestdb");

    }

    @Test
    public void ShouldCreateNewLearnerEntryToLearnerTable() throws Exception{

        Event event = new Event(new EventFixture().CREATE_USER_EVENT);

        UserManagementTask userManagementTask = new UserManagementTask();
        userManagementTask.init(configMock,contextMock);
        userManagementTask.processEvent(event,collectorMock);

        verify(collectorMock).send(argThat(validateOutputTopic(event.getMap(), SUCCESS_TOPIC)));

    }

    @Test
    public void ShouldCreateNewProfileEntryToProfileTable() throws Exception{

        Event event = new Event(new EventFixture().CREATE_PROFILE_EVENT);

        UserManagementTask userManagementTask = new UserManagementTask();
        userManagementTask.init(configMock,contextMock);
        userManagementTask.processEvent(event,collectorMock);

        verify(collectorMock).send(argThat(validateOutputTopic(event.getMap(), SUCCESS_TOPIC)));

    }

    @Test
    public void ShouldTestProcessMethodAndCreateNewLearnerEntry() throws Exception{
        Gson gson = new Gson();
        Event event = new Event(new EventFixture().CREATE_USER_EVENT);

        when(envelopMock.getMessage()).thenReturn(gson.fromJson(event.json,Map.class));

        UserManagementTask userManagementTask = new UserManagementTask();
        userManagementTask.init(configMock,contextMock);
        userManagementTask.process(envelopMock,collectorMock,coordinatorMock);

        verify(collectorMock).send(argThat(validateOutputTopic(event.getMap(), SUCCESS_TOPIC)));

    }

    private ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final Object message, final String stream) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
                assertEquals("kafka", systemStream.getSystem());
                assertEquals(stream, systemStream.getStream());
                assertEquals(message,outgoingMessageEnvelope.getMessage());
                return true;
            }
        };
    }
}
