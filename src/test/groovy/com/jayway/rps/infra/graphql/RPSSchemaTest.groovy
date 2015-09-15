package com.jayway.rps.infra.graphql

import com.jayway.rps.domain.Move
import com.jayway.rps.domain.game.GamesProjection
import graphql.GraphQL
import spock.lang.Specification

/**
 * Created by erik on 2015-09-15.
 */
class RPSSchemaTest extends Specification {

    private GamesProjection gameProjection;

    UUID game1Id = UUID.randomUUID();

    def setup() {
        gameProjection = new GamesProjection();

        GamesProjection.GameState game1 = new GamesProjection.GameState();
        game1.createdBy = "user1";
        game1.gameId = game1Id;
        game1.loser = "user1";
        game1.winner = "user2";
        game1.state = GamesProjection.State.won;
        game1.moves = new HashMap<>();
        game1.moves.put("user1", Move.rock);
        game1.moves.put("user2", Move.paper);


        gameProjection.getGames().put(game1Id, game1);

    }

    def "Get all games"() {

        when:
        def result = new GraphQL(RPSSchema.Schema).execute("{games{gameId}}", gameProjection).data;

        then:

        result == [games: [[gameId: game1Id.toString()]]];
    }

    def "Get all games with details"() {

        when:
        def result = new GraphQL(RPSSchema.Schema).execute("{games{gameId, createdBy, loser, winner, state, moves{user, move}}", gameProjection);

        then:

        result.data == [games: [[gameId: game1Id.toString(), createdBy: "user1", loser: "user1", winner: "user2", state: "won", moves: [[user: "user1", move:"rock"], [user: "user2", move:"paper"]]]]];
    }
}
