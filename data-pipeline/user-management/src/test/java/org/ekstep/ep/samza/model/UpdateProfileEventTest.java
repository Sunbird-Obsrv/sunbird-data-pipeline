package org.ekstep.ep.samza.model;

import com.zaxxer.hikari.HikariDataSource;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.UUID;


public class UpdateProfileEventTest {
    private HikariDataSource dataSource;
    private static final String uid = getRandomUID();


    @Before
    public void setUp(){
        String url = String.format("jdbc:mysql://%s:%s/%s", "0.0.0.0", "3306", "eptestdb");
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername("jenkins");
        dataSource.setPassword("ec0syst3m");
    }

    @Test
    public void ShouldCreateNewProfile() throws SQLException, ParseException {


        Event event = new Event(new EventFixture().CREATE_PROFILE_EVENT_1(uid));

        CreateProfileDto profileDto = new CreateProfileDto(dataSource);
        profileDto.process(event);

        Assert.assertEquals(true, profileDto.getIsInserted());

    }

    @Test
    public void ShouldUpdateTheProfile() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().UPDATE_PROFILE_EVENT_1(uid));

        UpdateProfileDto profileDto = new UpdateProfileDto(dataSource);
        profileDto.process(event);

        Assert.assertEquals(true, profileDto.getIsInserted());
    }

    @Test
    public void ShouldCreateNewProfileIfProfileDoesNotExistAndUpdateProfile() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().UPDATE_PROFILE_EVENT);

        UpdateProfileDto profileDto = new UpdateProfileDto(dataSource);
        profileDto.process(event);

        Assert.assertEquals(true, profileDto.getIsInserted());
        Assert.assertEquals(true, profileDto.isProfileExist(event.getUID()));
    }

    private static String getRandomUID(){
        return UUID.randomUUID().toString();
    }
}
