package com.jayway.rps.infra.rest;

import com.jayway.rps.domain.Move;

import java.util.HashMap;
import java.util.Map;

public class GameDTO {
	public String gameId;
	public String createdBy;
	public Map<String, Move> moves = new HashMap<String, Move>();
	public String state;
	public String winner;
	public String loser;
}
