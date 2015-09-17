package com.jayway.rps.infra.graphql;

import com.jayway.rps.domain.game.GamesProjection;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

import java.util.ArrayList;
import java.util.UUID;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * Created by erik on 2015-09-14.
 */
public class RPSSchema {


    public static GraphQLObjectType GameType = newObject()
            .name("Game")
            .field(newFieldDefinition()
                    .name("gameId")
                    .type(GraphQLString)
                    .fetchField()
                    .build())
            .field(newFieldDefinition()
                    .name("createdBy")
                    .type(GraphQLString)
                    .fetchField()
                    .build())
            .field(newFieldDefinition()
                    .name("moves")
                    .type(new GraphQLList(Types.MoveType))
                    .dataFetcher(environment -> new ArrayList<>(((GamesProjection.GameState) environment.getSource()).moves.entrySet()))
                    .fetchField()
                    .build())
            .field(newFieldDefinition()
                    .name("state")
                    .type(Types.StateType)
                    .fetchField()
                    .build())
            .field(newFieldDefinition()
                    .name("winner")
                    .type(GraphQLString)
                    .fetchField()
                    .build())
            .field(newFieldDefinition()
                    .name("loser")
                    .type(GraphQLString)
                    .fetchField()
                    .build())
            .build();


    public static GraphQLObjectType RPSQueryType = newObject()
            .name("RPSQuery")
            .field(newFieldDefinition()
                    .name("games")
                    .type(new GraphQLList(GameType))
                    .dataFetcher(environment -> new ArrayList<>(((GraphQLContext) environment.getContext()).gamesProjection.getGames().values()))
                    .build())
            .field(newFieldDefinition()
                    .name("game")
                    .type(GameType)
                    .argument(newArgument()
                            .name("id")
                            .description("id of the game")
                            .type(new GraphQLNonNull(GraphQLString))
                            .build())
                    .dataFetcher(environment -> ((GraphQLContext) environment.getContext()).gamesProjection.getGames().get(UUID.fromString(environment.getArgument("id"))))
                    .build())
            .build();

    public static GraphQLObjectType RPSMutationType = newObject()
            .name("RPSMutation")
            .field(newFieldDefinition()
                    .name("createGame")
                    .type(GameType)
                    .argument(newArgument()
                            .name("userId")
                            .description("id of the user creating the game")
                            .type(new GraphQLNonNull(GraphQLString))
                            .build())
                    .dataFetcher(environment -> {
                        GraphQLContext graphQLContext = (GraphQLContext) environment.getContext();
                        return graphQLContext.createGame(environment.getArgument("userId"));
                    })
                    .build())
            .field(newFieldDefinition()
                    .name("makeMove")
                    .type(GameType)
                    .argument(newArgument()
                            .name("gameId")
                            .description("id of the game")
                            .type(new GraphQLNonNull(GraphQLString))
                            .build())
                    .argument(newArgument()
                            .name("userId")
                            .description("id of the user making the move")
                            .type(new GraphQLNonNull(GraphQLString))
                            .build())
                    .argument(newArgument()
                            .name("move")
                            .description("the move being made")
                            .type(new GraphQLNonNull(GraphQLString))
                            .build())
                    .dataFetcher(environment -> {
                        GraphQLContext graphQLContext = (GraphQLContext) environment.getContext();
                        return graphQLContext.makeMove(environment.getArgument("gameId"), environment.getArgument("userId"), environment.getArgument("move"));
                    })
                    .build())
            .build();

    public static GraphQLSchema Schema = GraphQLSchema.newSchema()
            .query(RPSQueryType)
            .mutation(RPSMutationType)
            .build();
}
