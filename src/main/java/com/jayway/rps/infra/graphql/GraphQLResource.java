package com.jayway.rps.infra.graphql;

import com.jayway.rps.domain.game.GamesProjection;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping(value = "/graphql", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class GraphQLResource {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GraphQLResource.class);

    @Inject
    CommandGateway commandGateway;

    @Inject
    private GamesProjection gamesProjection;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity query(@RequestBody String query) {
        ExecutionResult result = new GraphQL(RPSSchema.Schema).execute(query, new GraphQLContext(gamesProjection, commandGateway));

        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
            result.getErrors().forEach(graphQLError -> log.error("GraphQL error: " + graphQLError.toString()));
            String errors = StringUtils.collectionToDelimitedString(result.getErrors(), "\n\r");
            throw new RuntimeException(String.format("Error when executing query '%1s': %2s", query, errors));
        }

        return ResponseEntity.ok(result.getData());
    }

}
