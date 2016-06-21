package org.ekstep.ep.samza.model;

import com.zaxxer.hikari.HikariDataSource;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.text.ParseException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class CreateProfileEventTest {
    private HikariDataSource dataSource;

    @Before
    public void setUp(){
        String url = String.format("jdbc:mysql://%s:%s/%s", "localhost", "3306", "eptestdb");
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername("jenkins");
        dataSource.setPassword("ec0syst3m");
    }

    @Test
    public void ShouldProcessEventAndCreateNewProfile() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().CREATE_PROFILE_EVENT);

        CreateProfileDto profileDto = new CreateProfileDto(dataSource);
        profileDto.process(event);

        assertEquals(true, profileDto.getIsInserted());

    }

    @Test
    public void ShouldProcessEventCreateNewProfileWithDateOfBirth() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().CREATE_PROFILE_EVENT_WITH_AGE);

        CreateProfileDto profileDto = new CreateProfileDto(dataSource);
        profileDto.process(event);

        assertEquals(true, profileDto.getIsInserted());
    }

    @Test
    public void ShouldProcessEventCreateNewProfileWithNoAge() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().CREATE_PROFILE_EVENT_WITH_NO_AGE);

        CreateProfileDto profileDto = new CreateProfileDto(dataSource);
        profileDto.process(event);

        assertEquals(true, profileDto.getIsInserted());
    }

    @Test
    public void ShouldCreateLearnerIfLeanerDoesNotExistWhileCreatingProfile() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().CREATE_PROFILE_EVENT_WITH_AGE);

        CreateProfileDto profileDto = new CreateProfileDto(dataSource);
        profileDto.process(event);

        assertEquals(true, profileDto.getIsInserted());
        assertEquals(true, profileDto.isLearnerExist(event.getUID(), event.id()));
    }

    @Test
    public void ShouldNotInsertIfEventIsInvalid() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().INVALID_PROFILE_EVENT);

        CreateProfileDto profileDto = new CreateProfileDto(dataSource);

        try {
            profileDto.process(event);
        }
        catch(Exception e){}

        assertEquals(false, profileDto.getIsInserted());
    }

    @Test
    public void ShouldInvokeDataSourceWithValidData() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().CREATE_PROFILE_EVENT_WITH_AGE);
        javax.sql.DataSource dataSourceMock = new Mockito().mock(javax.sql.DataSource.class);

        CreateProfileDto profileDto = new CreateProfileDto(dataSourceMock);

        Connection connectionMock = mock(Connection.class);
        stub(dataSourceMock.getConnection()).toReturn(connectionMock);
        PreparedStatement statementMock = mock(PreparedStatement.class);
        stub(connectionMock.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).toReturn(statementMock);

        profileDto.process(event);

        verify(statementMock).setString(2, "user@twitter.com");
        verify(statementMock).setInt(3, 2004);
        verify(statementMock).setString(4, "male");
        verify(statementMock).setInt(5, 10);
        verify(statementMock).setInt(6, 3);
        verify(statementMock).setString(7, "ML");
        verify(statementMock).setInt(8, 12);
        verify(statementMock).setInt(9, 11);
        verify(statementMock).setBoolean(10,false);

    }

    @Test
    public void ShouldCreateNewGroupUserProfile() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().CREATE_GROUP_USER_PROFILE_EVENT);
        javax.sql.DataSource dataSourceMock = mock(javax.sql.DataSource.class);

        CreateProfileDto profileDto = new CreateProfileDto(dataSourceMock);

        Connection connectionMock = mock(Connection.class);
        stub(dataSourceMock.getConnection()).toReturn(connectionMock);
        PreparedStatement statementMock = mock(PreparedStatement.class);
        stub(connectionMock.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).toReturn(statementMock);

        profileDto.process(event);

        verify(statementMock).setString(2, "user@twitter.com");
        verify(statementMock).setBoolean(10,true);

    }

}
