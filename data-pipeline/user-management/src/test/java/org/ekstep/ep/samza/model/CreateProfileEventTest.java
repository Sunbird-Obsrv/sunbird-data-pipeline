package org.ekstep.ep.samza.model;

import com.zaxxer.hikari.HikariDataSource;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.text.ParseException;


public class CreateProfileEventTest {
    private HikariDataSource dataSource;

    @Before
    public void setUp(){
        String url = String.format("jdbc:mysql://%s:%s/%s", "localhost", "3306", "eptestdb");
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername("root");
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

        Assert.assertEquals(false,profileDto.getIsInserted());
    }
}
