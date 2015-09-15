package com.jayway.rps.infra.graphql;

import com.jayway.rps.domain.game.GamesProjection;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping(value = "/graphql", produces = APPLICATION_JSON_VALUE)
public class GraphQLResource {

    @Inject
    CommandGateway commandGateway;

    @Inject
    private GamesProjection gamesProjection;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity query(@RequestBody String query) {
        ExecutionResult result = new GraphQL(RPSSchema.Schema).execute(query, new GraphQLContext(gamesProjection, commandGateway));

        return ResponseEntity.ok(result.getData());
    }

}
