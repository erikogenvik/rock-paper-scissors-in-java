package com.jayway.rps.infra.graphql

import com.jayway.rps.domain.Move
import com.jayway.rps.domain.game.GamesProjection
import graphql.GraphQL
import spock.lang.Specification

/**
 * Created by erik on 2015-09-15.
 */
class RPSRelaySchemaTest extends Specification {

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

    def "Validate Relay Node schema"() {

        given:
        def query = """{
                      __schema {
                        queryType {
                          fields {
                            name
                            type {
                              name
                              kind
                            }
                            args {
                              name
                              type {
                                kind
                                ofType {
                                  name
                                  kind
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                    """
        when:
        def result = new GraphQL(RelaySchema.Schema).execute(query, context);

        then:
        def nodeField = result.data["__schema"]["queryType"]["fields"][0];
        nodeField == [name: "node", type: [name: "Node", kind: "INTERFACE"], args: [[name: "id", type: [kind: "NON_NULL", ofType: [name: "ID", kind: "SCALAR"]]]]]
    }


    def "Validate Relay GameConnection schema"() {

        given:
        def query = """{
                          __type(name: "GameConnection") {
                            fields {
                              name
                              type {
                                name
                                kind
                                ofType {
                                  name
                                  kind
                                }
                              }
                            }
                          }
                        }"""
        when:
        def result = new GraphQL(RelaySchema.Schema).execute(query, context);

        then:
        def fields = result.data["__type"]["fields"];
        fields == [[name: "edges", type: [name: null, kind: "LIST", ofType: [name: "GameEdge", kind: "OBJECT"]]], [name: "pageInfo", type: [name: null, kind: "NON_NULL", ofType: [name: "PageInfo", kind: "OBJECT"]]]]
    }

    def "Validate Relay GameEdge schema"() {

        given:
        def query = """{
                          __type(name: "GameEdge") {
                            fields {
                              name
                              type {
                                name
                                kind
                                ofType {
                                  name
                                  kind
                                }
                              }
                            }
                          }
                        }
                    """
        when:
        def result = new GraphQL(RelaySchema.Schema).execute(query, context);

        then:
        def fields = result.data["__type"]["fields"];
        fields == [[name: "node", type: [name: "Game", kind: "OBJECT", ofType: null]], [name: "cursor", type: [name: null, kind: "NON_NULL", ofType: [name: "String", kind: "SCALAR"]]]]
    }

    def "Query for games"() {
        given:
        def query = """query GamesQuery{viewer{games{edges{node{...__RelayQueryFragment0561emi},cursor},pageInfo{hasNextPage,hasPreviousPage}}}} fragment __RelayQueryFragment0561emi on Game{gameId}"""

        when:
        def result = new GraphQL(RelaySchema.Schema).execute(query, context);

        then:
        result.data == [viewer: [games: [edges: [[node: [gameId: game1Id.toString()], cursor: "CURSOR_0"], [node: [gameId: game2Id.toString()], cursor: "CURSOR_1"]], pageInfo: [hasNextPage: false, hasPreviousPage: false]]]]
    }
}
