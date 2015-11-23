package com.jayway.rps.infra.graphql;

import com.jayway.rps.domain.game.GamesProjection;

/**
 * Created by erik on 2015-10-16.
 */
public class CreateGamePayload {
    public GamesProjection.GameState game;
    public String clientMutationId;
}
