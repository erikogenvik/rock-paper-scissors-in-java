package com.jayway.rps.infra.graphql;

import com.jayway.rps.domain.game.GamesProjection;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.spi.LoggerFactory;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping(value = "/relay")
public class RelayResource {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RelayResource.class);

    @Inject
    CommandGateway commandGateway;

    @Inject
    private GamesProjection gamesProjection;

    @Inject
    private ApplicationContext appContext;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity query(@RequestBody @Valid RelaySpec querySpec) {
        ExecutionResult result = new GraphQL(RelaySchema.Schema).execute(querySpec.query, new GraphQLContext(gamesProjection, commandGateway));

        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
            result.getErrors().forEach(graphQLError -> log.error("GraphQL error: " + graphQLError.toString()));
            String errors = StringUtils.collectionToDelimitedString(result.getErrors(), "\n\r");
            throw new RuntimeException(String.format("Error when executing query '%1s': %2s", querySpec.query, errors));
        }
        return ResponseEntity.ok(result.getData());
    }

    @RequestMapping(value = "/schema", method = RequestMethod.GET)
    public ResponseEntity query() throws IOException {

        Resource resource = appContext.getResource("classpath:introspectionQuery.graphql");
        try (InputStream inputStream = resource.getInputStream()) {
            String query = IOUtils.toString(inputStream);
            ExecutionResult result = new GraphQL(RelaySchema.Schema).execute(query, new GraphQLContext(gamesProjection, commandGateway));

            return ResponseEntity.ok(result.getData());
        }
    }

}
