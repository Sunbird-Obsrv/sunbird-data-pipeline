package org.ekstep.ep.samza.model;

import com.zaxxer.hikari.HikariDataSource;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.activation.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.util.Date;


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

        Assert.assertEquals(true, profileDto.getIsInserted());

    }

    @Test
    public void ShouldProcessEventCreateNewProfileWithDateOfBirth() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().CREATE_PROFILE_EVENT_WITH_AGE);

        CreateProfileDto profileDto = new CreateProfileDto(dataSource);
        profileDto.process(event);

        Assert.assertEquals(true, profileDto.getIsInserted());
    }

    @Test
    public void ShouldCreateLearnerIfLeanerDoesNotExistWhileCreatingProfile() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().CREATE_PROFILE_EVENT_WITH_AGE);

        CreateProfileDto profileDto = new CreateProfileDto(dataSource);
        profileDto.process(event);

        Assert.assertEquals(true, profileDto.getIsInserted());
        Assert.assertEquals(true, profileDto.isLearnerExist(event.getUID()));
    }

    @Test
    public void ShouldNotInsertIfEventIsInvalid() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().INVALID_PROFILE_EVENT);

        CreateProfileDto profileDto = new CreateProfileDto(dataSource);

        try {
            profileDto.process(event);
        }
        catch(Exception e){}

        Assert.assertEquals(false, profileDto.getIsInserted());
    }

    @Test
    public void ShouldInvokeDataSourceWithValidData() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().CREATE_PROFILE_EVENT_WITH_AGE);
        javax.sql.DataSource dataSourceMock = new Mockito().mock(javax.sql.DataSource.class);

        CreateProfileDto profileDto = new CreateProfileDto(dataSourceMock);

        Connection connectionMock = Mockito.mock(Connection.class);
        Mockito.stub(dataSourceMock.getConnection()).toReturn(connectionMock);
        PreparedStatement statementMock = Mockito.mock(PreparedStatement.class);
        Mockito.stub(connectionMock.prepareStatement(Mockito.anyString(), Mockito.eq(Statement.RETURN_GENERATED_KEYS))).toReturn(statementMock);

        profileDto.process(event);

        Mockito.verify(statementMock).setNString(2, "user@twitter.com");
        Mockito.verify(statementMock).setInt(3, 2004);
        Mockito.verify(statementMock).setString(4, "male");
        Mockito.verify(statementMock).setInt(5, 10);
        Mockito.verify(statementMock).setInt(6, 3);
        Mockito.verify(statementMock).setString(7, "ML");

    }
}
