package com.jayway.rps;

import com.jayway.rps.domain.Move;
import com.jayway.rps.domain.command.CreateGameCommand;
import com.jayway.rps.domain.command.MakeMoveCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by erik on 2015-09-15.
 */
@Component
public class TestDataGenerator {


    @Inject
    CommandGateway commandGateway;

    private boolean hasSetupData;

    public static final UUID game1Id = UUID.randomUUID();
    public static final UUID game2Id = UUID.randomUUID();
    public static final String user1Id = "user1";
    public static final String user2Id = "user2";

    public void setupTestData() {
        if (!hasSetupData) {
            commandGateway.sendAndWait(new CreateGameCommand(game1Id, user1Id));
            commandGateway.sendAndWait(new MakeMoveCommand(game1Id, user1Id, Move.paper));
            commandGateway.sendAndWait(new MakeMoveCommand(game1Id, user2Id, Move.rock));

            commandGateway.sendAndWait(new CreateGameCommand(game2Id, user1Id));
            commandGateway.sendAndWait(new MakeMoveCommand(game2Id, user1Id, Move.paper));
            hasSetupData = true;
        }
    }
}
