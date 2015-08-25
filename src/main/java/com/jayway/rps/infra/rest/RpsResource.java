package com.jayway.rps.infra.rest;

import com.jayway.rps.domain.Move;
import com.jayway.rps.domain.command.CreateGameCommand;
import com.jayway.rps.domain.command.MakeMoveCommand;
import com.jayway.rps.domain.game.GamesProjection;
import com.jayway.rps.domain.game.GamesProjection.GameState;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/v1/games")
public class RpsResource {

    @Inject
    CommandGateway commandGateway;

    @Inject
    private GamesProjection gamesProjection;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createGame(@RequestHeader("SimpleIdentity") String email) throws Exception {
        UUID gameId = UUID.randomUUID();
        commandGateway.send(new CreateGameCommand(gameId, email));
        return ResponseEntity.created(linkTo(methodOn(RpsResource.class).game(gameId.toString())).toUri()).build();
    }

    @RequestMapping(value = "/{gameId}", produces = APPLICATION_JSON_VALUE, consumes = "*")
    public ResponseEntity<GameDTO> game(
            @PathVariable("gameId") String gameId) {
        GameState gameState = gamesProjection.get(UUID.fromString(gameId));
        GameDTO dto = new GameDTO();
        dto.gameId = gameState.gameId.toString();
        dto.createdBy = gameState.createdBy;
        dto.state = gameState.state.toString();
        if (gameState.state.completed) {
            dto.winner = gameState.winner;
            dto.loser = gameState.loser;
            dto.moves = gameState.moves;
        }
        return ResponseEntity.ok(dto);
    }

    @RequestMapping(value = "/{gameId}", method = RequestMethod.POST)
    public void makeMove(
            @PathVariable("gameId") String gameId,
            @RequestHeader("SimpleIdentity") String email,
            @RequestParam("move") Move move) throws Exception {

        commandGateway.send(new MakeMoveCommand(UUID.fromString(gameId), email, move));
    }
}
