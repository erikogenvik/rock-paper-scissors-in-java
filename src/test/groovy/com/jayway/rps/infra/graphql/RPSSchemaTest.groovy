package com.jayway.rps.infra.graphql

import com.jayway.rps.domain.Move
import com.jayway.rps.domain.event.GameCreatedEvent
import com.jayway.rps.domain.event.MoveDecidedEvent
import com.jayway.rps.domain.game.GamesProjection
import graphql.GraphQL
import spock.lang.Specification

/**
 * Created by erik on 2015-09-15.
 */
class RPSSchemaTest extends Specification {

    private GamesProjection gameProjection;
    private GraphQLContext context;

    UUID game1Id = UUID.randomUUID();
    UUID game2Id = UUID.randomUUID();

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

        GamesProjection.GameState game2 = new GamesProjection.GameState();
        game2.createdBy = "user2";
        game2.gameId = game2Id;
        game2.state = GamesProjection.State.inProgress;
        game2.moves = new HashMap<>();
        game2.moves.put("user1", Move.rock);

        gameProjection.getGames().put(game1Id, game1);
        gameProjection.getGames().put(game2Id, game2);


        context = new GraphQLContext(gameProjection, null);
    }

    def "Get all games"() {

        when:
        def result = new GraphQL(RPSSchema.Schema).execute("{games{gameId}}", context).data;

        then:

        result == [games: [[gameId: game1Id.toString()], [gameId: game2Id.toString()]]];
    }

    def "Get one game"() {

        when:
        def query = """{game(id: "${game1Id}") {gameId}}"""
        def result = new GraphQL(RPSSchema.Schema).execute(query, context).data;

        then:

        result == [game: [gameId: game1Id.toString()]];
    }

    def "Get one game with fragment"() {

        when:
        def query = """query Q{game(id: "${game1Id}") {...gameFields}}
                        fragment gameFields on Game { gameId }"""
        def result = new GraphQL(RPSSchema.Schema).execute(query, context).data;

        then:

        result == [game: [gameId: game1Id.toString()]];
    }

    def "Get one game with details with fragments"() {

        when:
        def query = """query Q{game(id: "${game1Id}") {...gameFields, ...gameDetailsFields}}
                        fragment gameFields on Game { gameId }
                        fragment gameDetailsFields on Game { createdBy, loser, winner }"""
        def result = new GraphQL(RPSSchema.Schema).execute(query, context).data;

        then:

        result == [game: [gameId: game1Id.toString(), createdBy: "user1", loser: "user1", winner: "user2"]];
    }

    def "Get all games with details"() {

        when:
        def result = new GraphQL(RPSSchema.Schema).execute("{games{gameId, createdBy, loser, winner, state, moves{user, move}}", context);

        then:

        result.data == [games: [
                [gameId: game1Id.toString(), createdBy: "user1", loser: "user1", winner: "user2", state: "won", moves: [[user: "user1", move: "rock"], [user: "user2", move: "paper"]]],
                [gameId: game2Id.toString(), createdBy: "user2", loser: null, winner: null, state: "inProgress", moves: [[user: "user1", move: "rock"]]]
        ]];
    }

    def "Add game"() {

        given:

        context = Mock(GraphQLContext)
        context.gamesProjection >> gameProjection
        context.createGame(_) >> {
            UUID gameId = UUID.randomUUID()
            gameProjection.handle(new GameCreatedEvent(gameId, "user1"))
            return gameProjection.get(game1Id)
        }

        when:
        def mutation = """mutation M{ game: createGame(userId: "user1") {gameId}}"""
        def result = new GraphQL(RPSSchema.Schema).execute(mutation, context);

        then:

        gameProjection.getGames().size() == 3

        result.data == [game: [gameId: gameProjection.getGames().entrySet().first().key.toString()]];
    }

    def "Make move"() {

        given:

        context = Mock(GraphQLContext)
        context.gamesProjection >> gameProjection
        context.makeMove(_, _, _) >> {
            gameProjection.handle(new MoveDecidedEvent(game1Id, "user2", Move.paper))
            return gameProjection.get(game1Id)
        }

        when:
        def mutation = """mutation M{ game: makeMove(userId: "user1", gameId:"${
            game1Id.toString()
        }", move: "paper") {gameId, moves{user, move}}}"""
        def result = new GraphQL(RPSSchema.Schema).execute(mutation, context);

        then:


        result.data == [game: [gameId: game1Id.toString(), moves: [[user: "user1", move: "rock"], [user: "user2", move: "paper"]]]];
    }
}
