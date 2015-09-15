package com.jayway.rps.infra.graphql;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.http.ContentType;
import com.jayway.rps.TestDataGenerator;
import com.jayway.rps.app.RpsMain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static com.jayway.restassured.RestAssured.with;
import static org.hamcrest.Matchers.*;

/**
 * Created by erik on 2015-09-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RpsMain.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class GraphQLResourceTest {
    @Value("${local.server.port}")
    int port;

    @Inject
    TestDataGenerator testDataGenerator;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        testDataGenerator.setupTestData();
    }

    @Test
    public void whenGetAllGamesIdAreAllReturned() {
        String query = "{games{gameId}}";

        with().body(query)
                .post("/graphql")
                .then().assertThat()
                .body("games.gameId", containsInAnyOrder(TestDataGenerator.game1Id.toString(), TestDataGenerator.game2Id.toString()));

    }

    @Test
    public void whenGetAllGamesFullAreAllReturned() {
        String query = "{games{gameId, createdBy, loser, winner, state, moves{user, move}}";


        with().body(query)
                .post("/graphql")
                .then().assertThat()
                .body("games[0].gameId", equalTo(TestDataGenerator.game1Id.toString()))
                .body("games[0].loser", equalTo(TestDataGenerator.user2Id))
                .body("games[0].winner", equalTo(TestDataGenerator.user1Id))
                .body("games[0].state", equalTo("won"))
                .body("games[0].moves[0].user", equalTo(TestDataGenerator.user1Id))
                .body("games[0].moves[0].move", equalTo("paper"))
                .body("games[0].moves[1].user", equalTo(TestDataGenerator.user2Id))
                .body("games[0].moves[1].move", equalTo("rock"))
                .body("games[1].gameId", equalTo(TestDataGenerator.game2Id.toString()))
                .body("games[1].loser", is(nullValue()))
                .body("games[1].winner", is(nullValue()))
                .body("games[1].state", equalTo("inProgress"))
                .body("games[1].moves[0].user", equalTo(TestDataGenerator.user1Id))
                .body("games[1].moves[0].move", equalTo("paper"));

    }

}
