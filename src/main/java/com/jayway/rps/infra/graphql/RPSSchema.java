package com.jayway.rps.infra.graphql;

import com.jayway.rps.domain.Move;
import com.jayway.rps.domain.game.GamesProjection;
import graphql.schema.*;

import java.util.ArrayList;
import java.util.Map;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * Created by erik on 2015-09-14.
 */
public class RPSSchema {

    public static GraphQLEnumType MoveEnumType = newEnum()
            .name("MoveEnum")
            .value("rock", Move.rock)
            .value("paper", Move.paper)
            .value("scissor", Move.scissor)
            .build();

    public static GraphQLObjectType MoveType = newObject()
            .name("Move")
            .field(newFieldDefinition()
                    .name("user")
                    .type(GraphQLString)
                    .dataFetcher(environment -> ((Map.Entry<String, Move>)environment.getSource()).getKey())
                    .build())
            .field(newFieldDefinition()
                    .name("move")
                    .type(MoveEnumType)
                    .dataFetcher(environment -> ((Map.Entry<String, Move>) environment.getSource()).getValue())
                    .build())
            .build();

    public static GraphQLEnumType StateType = newEnum()
            .name("State")
            .value("inProgress", GamesProjection.State.inProgress)
            .value("won", GamesProjection.State.won)
            .value("tied", GamesProjection.State.tied)
            .build();


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
                    .type(new GraphQLList(MoveType))
                    .dataFetcher(environment -> new ArrayList<>(((GamesProjection.GameState) environment.getSource()).moves.entrySet()))
                    .fetchField()
                    .build())
            .field(newFieldDefinition()
                    .name("state")
                    .type(StateType)
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


    public static GraphQLObjectType RPSType = newObject()
            .name("RPS")
            .field(newFieldDefinition()
                    .name("games")
                    .type(new GraphQLList(GameType))
                    .dataFetcher(environment -> new ArrayList<>(((GamesProjection) environment.getSource()).getGames().values()))
                    .build())
            .build();

    public static GraphQLSchema Schema = GraphQLSchema.newSchema()
            .query(RPSType)
            .build();
}
