package com.jayway.rps.domain.event;

import java.util.UUID;

public class GameTiedEvent  {
	public final UUID gameId;
	
	GameTiedEvent() {
		gameId = null;
	}

	public GameTiedEvent(UUID gameId) {
		this.gameId = gameId;
	}
	
}
