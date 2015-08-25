package com.jayway.rps.domain.command;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

import java.util.UUID;

public class CreateGameCommand {
	@TargetAggregateIdentifier
	public final UUID gameId;
	public final String playerEmail;
	
	public CreateGameCommand(UUID gameId, String playerEmail) {
		if (gameId == null) throw new IllegalArgumentException("gameId must not be null");
		if (playerEmail == null) throw new IllegalArgumentException("playerEmail must not be null");
		this.gameId = gameId;
		this.playerEmail = playerEmail;
	}

	public UUID aggregateId() {
		return gameId;
	}
	
}
