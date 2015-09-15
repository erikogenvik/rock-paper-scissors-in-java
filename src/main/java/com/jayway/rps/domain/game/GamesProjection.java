package com.jayway.rps.domain.game;

import com.jayway.rps.domain.Move;
import com.jayway.rps.domain.event.GameCreatedEvent;
import com.jayway.rps.domain.event.GameTiedEvent;
import com.jayway.rps.domain.event.GameWonEvent;
import com.jayway.rps.domain.event.MoveDecidedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class GamesProjection {
    public enum State {
        inProgress(false),
        won(true),
        tied(true);

        public final boolean completed;

        State(boolean completed) {
            this.completed = completed;
        }
    }

    public static class GameState {
        public UUID gameId;
        public String createdBy;
        public Map<String, Move> moves = new LinkedHashMap<>();
        public State state;
        public String winner;
        public String loser;
    }

    public Map<UUID, GameState> getGames() {
        return games;
    }

    private Map<UUID, GameState> games = new LinkedHashMap<>();

    public GameState get(UUID gameId) {
        return games.get(gameId);
    }


    @EventHandler
    public void handle(GameCreatedEvent e) {
        GameState game = new GameState();
        game.gameId = e.gameId;
        game.state = State.inProgress;
        game.createdBy = e.playerEmail;
        games.put(e.gameId, game);
    }

    @EventHandler
    public void handle(MoveDecidedEvent e) {
        GameState game = games.get(e.gameId);
        game.moves.put(e.playerEmail, e.move);
    }

    @EventHandler
    public void handle(GameWonEvent e) {
        GameState game = games.get(e.gameId);
        game.state = State.won;
        game.winner = e.winnerEmail;
        game.loser = e.loserEmail;
    }

    @EventHandler
    public void handle(GameTiedEvent e) {
        GameState game = games.get(e.gameId);
        game.state = State.tied;
    }
}
