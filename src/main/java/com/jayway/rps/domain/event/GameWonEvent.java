package com.jayway.rps.domain.event;

import java.util.UUID;

public class GameWonEvent  {
	public final UUID gameId;
	public final String winnerEmail;
	public final String loserEmail;

	public GameWonEvent(UUID gameId, String winnerEmail, String loserEmail) {
		this.gameId = gameId;
		this.winnerEmail = winnerEmail;
		this.loserEmail = loserEmail;
	}
	
	public GameWonEvent() {
		gameId = null;
		winnerEmail = null;
		loserEmail = null;
	}
}
