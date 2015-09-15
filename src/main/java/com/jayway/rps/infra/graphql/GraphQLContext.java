package com.jayway.rps.infra.graphql;

import com.jayway.rps.domain.command.CreateGameCommand;
import com.jayway.rps.domain.game.Game;
import com.jayway.rps.domain.game.GamesProjection;
import org.axonframework.commandhandling.gateway.CommandGateway;

import java.util.UUID;

/**
 * Created by erik on 2015-09-15.
 */
public class GraphQLContext {

    public final GamesProjection gamesProjection;
    private final CommandGateway commandGateway;

    public GraphQLContext(GamesProjection gamesProjection, CommandGateway commandGateway) {
        this.gamesProjection = gamesProjection;
        this.commandGateway = commandGateway;
    }

    public GamesProjection.GameState createGame(String userId) {
        UUID gameId = UUID.randomUUID();
        commandGateway.sendAndWait(new CreateGameCommand(gameId, userId));
        return gamesProjection.get(gameId);
    }
}
