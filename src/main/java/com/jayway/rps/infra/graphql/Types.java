package com.jayway.rps.infra.graphql;

import com.jayway.rps.domain.Move;
import com.jayway.rps.domain.game.GamesProjection;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLObjectType;

import java.util.Map;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * Created by erik on 2015-09-17.
 */
public class Types {

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
                    .dataFetcher(environment -> ((Map.Entry<String, Move>) environment.getSource()).getKey())
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
}
