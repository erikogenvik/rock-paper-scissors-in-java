package com.jayway.rps.domain.event;

import java.util.UUID;

public class GameCreatedEvent {
	public final UUID gameId;
	public final String playerEmail;
	
	GameCreatedEvent() {
	    gameId = null;
	    playerEmail = null;
    }
	
	public GameCreatedEvent(UUID gameId, String playerEmail) {
		this.gameId = gameId;
		this.playerEmail = playerEmail;
	}
}
