package ma.oumouou.cqrsesaxon.commands.controllers;

import ma.oumouou.cqrsesaxon.commands.commands.AccountCommandError;
import ma.oumouou.cqrsesaxon.commands.commands.AddAccountCommand;
import ma.oumouou.cqrsesaxon.commands.commands.CreditAccountCommand;
import ma.oumouou.cqrsesaxon.commands.commands.DebitAccountCommand;
import ma.oumouou.cqrsesaxon.commands.commands.UpdateAccountStatusCommand;
import ma.oumouou.cqrsesaxon.commands.dtos.AccountEventResponse;
import ma.oumouou.cqrsesaxon.commands.dtos.AddNewAccountRequestDto;
import ma.oumouou.cqrsesaxon.commands.dtos.CreditAccountRequestDto;
import ma.oumouou.cqrsesaxon.commands.dtos.DebitAccountRequestDto;
import ma.oumouou.cqrsesaxon.commands.dtos.UpdateAccountStatusRequestDto;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping({"/commands/accounts", "/command/accounts"})
public class AccountCommandController {
    private final CommandGateway commandGateway;
    private final EventStore eventStore;

    public AccountCommandController(CommandGateway commandGateway, EventStore eventStore) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
    }

    @PostMapping("/add")
    public CompletableFuture<String> addNewAccount(@RequestBody AddNewAccountRequestDto request) {
        String accountId = UUID.randomUUID().toString();
        return commandGateway.send(new AddAccountCommand(accountId, request.initialBalance(), request.currency()))
                .thenApply(result -> accountId);
    }

    @PostMapping("/credit")
    public CompletableFuture<Void> creditAccount(@RequestBody CreditAccountRequestDto request) {
        return commandGateway.send(new CreditAccountCommand(request.accountId(), request.amount(), request.currency()));
    }

    @PostMapping("/debit")
    public CompletableFuture<Void> debitAccount(@RequestBody DebitAccountRequestDto request) {
        return commandGateway.send(new DebitAccountCommand(request.accountId(), request.amount(), request.currency()));
    }

    @PutMapping("/updateStatus")
    public CompletableFuture<Void> updateAccountStatus(@RequestBody UpdateAccountStatusRequestDto request) {
        return commandGateway.send(new UpdateAccountStatusCommand(request.accountId(), request.accountStatus()));
    }

    @GetMapping("/events/{accountId}")
    public List<AccountEventResponse> getEvents(@PathVariable String accountId) {
        try (var events = eventStore.readEvents(accountId).asStream()) {
            return events.map(event -> new AccountEventResponse(event.getAggregateIdentifier(), event.getSequenceNumber(),
                    event.getTimestamp(), event.getPayloadType().getSimpleName(), event.getPayload())).toList();
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String invalidCommand(IllegalArgumentException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(AggregateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String accountNotFound(AggregateNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(CommandExecutionException.class)
    public ResponseEntity<String> commandFailed(CommandExecutionException exception) {
        Object details = exception.getDetails().orElse(null);
        if (details == AccountCommandError.INVALID_COMMAND) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
        if (details == AccountCommandError.ACCOUNT_NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
        return ResponseEntity.internalServerError().build();
    }
}
