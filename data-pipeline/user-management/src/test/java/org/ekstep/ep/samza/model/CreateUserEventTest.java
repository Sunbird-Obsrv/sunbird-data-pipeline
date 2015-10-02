package org.ekstep.ep.samza.model;

import com.zaxxer.hikari.HikariDataSource;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.text.ParseException;


public class CreateUserEventTest {
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
    public void ShouldProcessEventAndInsertNewEntry() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().CREATE_USER_EVENT);

        CreateLearnerDto learnerDto = new CreateLearnerDto(dataSource);
        learnerDto.process(event);

        Assert.assertEquals(true,learnerDto.getIsInserted());
    }

    @Test
    public void ShouldNotInsertIfUidIsNull() throws SQLException, ParseException {
        Event event = new Event(new EventFixture().INVALID_CREATE_EVENT);
        CreateLearnerDto learnerDto = new CreateLearnerDto(dataSource);
        try {
            learnerDto.process(event);
        }
        catch (Exception e){

        }
        Assert.assertEquals(false,learnerDto.getIsInserted());
    }
}
