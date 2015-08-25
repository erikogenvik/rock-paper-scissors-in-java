package com.jayway.rps.game;

import com.jayway.rps.app.RpsConfig;
import com.jayway.rps.domain.command.CreateGameCommand;
import com.jayway.rps.domain.game.Game;
import com.jayway.rps.domain.game.GamesProjection;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.test.FixtureConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 * Created by erik on 2015-08-25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RpsConfig.class)
public class GameProjectionTest {


    private FixtureConfiguration<Game> fixture;

    @Inject
    GamesProjection projection;

    @Inject
    CommandGateway commandGateway;


    UUID gameId = UUID.randomUUID();
    String player1 = UUID.randomUUID().toString();
    String player2 = UUID.randomUUID().toString();

    @Test
    @DirtiesContext
    public void createGame() {
        commandGateway.send(new CreateGameCommand(gameId, player1));

        assertNotNull(projection.get(gameId));
    }
}
