package com.jayway.rps.game;

import com.jayway.rps.domain.Move;
import com.jayway.rps.domain.command.CreateGameCommand;
import com.jayway.rps.domain.command.MakeMoveCommand;
import com.jayway.rps.domain.event.GameCreatedEvent;
import com.jayway.rps.domain.event.GameTiedEvent;
import com.jayway.rps.domain.event.GameWonEvent;
import com.jayway.rps.domain.event.MoveDecidedEvent;
import com.jayway.rps.domain.game.Game;
import org.axonframework.repository.AggregateNotFoundException;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class GameIntegrationTest {

    private FixtureConfiguration<Game> fixture;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(Game.class);
    }


    UUID gameId = UUID.randomUUID();
    String player1 = UUID.randomUUID().toString();
    String player2 = UUID.randomUUID().toString();

    @Test
    public void createGame() {
        fixture.given()
                .when(new CreateGameCommand(gameId, player1))
                .expectEvents(new GameCreatedEvent(gameId, player1));
    }

    @Test
    public void tie() throws Exception {
        fixture.givenCommands(new CreateGameCommand(gameId, player1),
                new MakeMoveCommand(gameId, player1, Move.rock))
                .when(new MakeMoveCommand(gameId, player2, Move.rock))
                .expectEvents(new MoveDecidedEvent(gameId, player2, Move.rock), new GameTiedEvent(gameId));
    }

    @Test
    public void victory() throws Exception {
        fixture.givenCommands(new CreateGameCommand(gameId, player1),
                new MakeMoveCommand(gameId, player1, Move.rock))
                .when(new MakeMoveCommand(gameId, player2, Move.paper))
                .expectEvents(new MoveDecidedEvent(gameId, player2, Move.paper), new GameWonEvent(gameId, player2, player1));
    }

    @Test
    public void same_player_should_fail() throws Exception {
        fixture.givenCommands(new CreateGameCommand(gameId, player1),
                new MakeMoveCommand(gameId, player1, Move.rock))
                .when(new MakeMoveCommand(gameId, player1, Move.rock))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    public void game_not_started() throws Exception {
        fixture.given()
                .when(new MakeMoveCommand(gameId, player1, Move.rock))
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    public void move_after_end_should_fail() {
        fixture.givenCommands(new CreateGameCommand(gameId, player1),
                new MakeMoveCommand(gameId, player1, Move.rock),
                new MakeMoveCommand(gameId, player2, Move.rock))
                .when(new MakeMoveCommand(gameId, "another", Move.rock))
                .expectException(IllegalStateException.class);
    }

}
