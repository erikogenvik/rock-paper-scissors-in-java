package com.jayway.rps.domain.game;

import com.jayway.rps.domain.Move;
import com.jayway.rps.domain.command.CreateGameCommand;
import com.jayway.rps.domain.command.MakeMoveCommand;
import com.jayway.rps.domain.event.GameCreatedEvent;
import com.jayway.rps.domain.event.GameTiedEvent;
import com.jayway.rps.domain.event.GameWonEvent;
import com.jayway.rps.domain.event.MoveDecidedEvent;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

import java.util.UUID;

public class Game extends AbstractAnnotatedAggregateRoot<UUID> {
    enum State {
        notInitalized, created, waiting, tied, won
    }

    @AggregateIdentifier
    private UUID identifier;

    private State state = State.notInitalized;
    private String player;
    private Move move;

    public Game() {

    }

    @CommandHandler
    public Game(CreateGameCommand c) {
        if (state != State.notInitalized) throw new IllegalStateException(state.toString());

        apply(new GameCreatedEvent(c.gameId, c.playerEmail));
    }

    @CommandHandler
    public void handle(MakeMoveCommand c) {
        if (State.created == state) {
            apply(new MoveDecidedEvent(c.gameId, c.playerEmail, c.move));
        } else if (State.waiting == state) {
            if (player.equals(c.playerEmail)) throw new IllegalArgumentException("Player already in game");
            apply(new MoveDecidedEvent(c.gameId, c.playerEmail, c.move));
            apply(makeEndGameEvent(c.gameId, c.playerEmail, c.move));
        } else {
            throw new IllegalStateException(state.toString());
        }
    }

    private Object makeEndGameEvent(UUID gameId, String opponentEmail, Move opponentMove) {
        if (move.defeats(opponentMove)) {
            return new GameWonEvent(gameId, player, opponentEmail);
        } else if (opponentMove.defeats(move)) {
            return new GameWonEvent(gameId, opponentEmail, player);
        } else {
            return new GameTiedEvent(gameId);
        }
    }


    @EventSourcingHandler
    public void handle(MoveDecidedEvent e) {
        if (state == State.created) {
            move = e.move;
            player = e.playerEmail;
            state = State.waiting;
        }
    }

    @EventSourcingHandler
    public void handle(GameWonEvent e) {
        state = State.won;
    }

    @EventSourcingHandler
    public void handle(GameTiedEvent e) {
        state = State.tied;
    }

    @EventSourcingHandler
    public void on(GameCreatedEvent event) {
        state = State.created;
        this.identifier = event.gameId;
    }

}
