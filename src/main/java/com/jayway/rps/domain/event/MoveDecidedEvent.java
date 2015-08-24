package com.jayway.rps.domain.event;

import com.jayway.rps.domain.Move;

import java.util.UUID;

public class MoveDecidedEvent {

    public final UUID gameId;
    public final String playerEmail;
    public final Move move;

    public MoveDecidedEvent(UUID gameId, String playerEmail, Move move) {
        this.gameId = gameId;
        this.playerEmail = playerEmail;
        this.move = move;
    }

    public MoveDecidedEvent() {
        gameId = null;
        playerEmail = null;
        move = null;
    }

}
