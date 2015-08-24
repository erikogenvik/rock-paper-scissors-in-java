package com.jayway.rps.domain.command;

import com.jayway.rps.domain.Move;

import java.util.UUID;

public class MakeMoveCommand {
    public final UUID gameId;
    public final String playerEmail;
    public final Move move;

    public MakeMoveCommand(UUID gameId, String playerEmail, Move move) {
        if (gameId == null) throw new IllegalArgumentException("gameId must not be null");
        if (playerEmail == null) throw new IllegalArgumentException("playerEmail must not be null");
        if (move == null) throw new IllegalArgumentException("move must not be null");
        this.gameId = gameId;
        this.playerEmail = playerEmail;
        this.move = move;

    }

    public UUID aggregateId() {
        return gameId;
    }
}
