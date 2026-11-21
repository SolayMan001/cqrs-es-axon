package ma.oumouou.cqrsesaxon.query.controllers;

import ma.oumouou.cqrsesaxon.query.dtos.AccountListResponseDto;
import ma.oumouou.cqrsesaxon.query.dtos.AccountStatementResponseDto;
import ma.oumouou.cqrsesaxon.query.entities.Account;
import ma.oumouou.cqrsesaxon.query.entities.AccountOperation;
import ma.oumouou.cqrsesaxon.query.queries.GetAccountStatementQuery;
import ma.oumouou.cqrsesaxon.query.queries.GetAllAccountsQuery;
import ma.oumouou.cqrsesaxon.query.queries.WatchEventQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/query/accounts")
public class AccountQueryController {
    private final QueryGateway queryGateway;

    public AccountQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/all")
    public CompletableFuture<List<Account>> getAllAccounts() {
        return queryGateway.query(new GetAllAccountsQuery(), ResponseTypes.instanceOf(AccountListResponseDto.class))
                .thenApply(AccountListResponseDto::accounts);
    }

    @GetMapping("/accountStatement/{accountId}")
    public CompletableFuture<ResponseEntity<AccountStatementResponseDto>> getAccountStatement(@PathVariable String accountId) {
        return queryGateway.query(new GetAccountStatementQuery(accountId), ResponseTypes.instanceOf(AccountStatementResponseDto.class))
                .thenApply(statement -> ResponseEntity.of(Optional.ofNullable(statement)));
    }

    @GetMapping(value = "/watch/{accountId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AccountOperation> watch(@PathVariable String accountId) {
        return Flux.defer(() -> {
            SubscriptionQueryResult<AccountOperation, AccountOperation> result = queryGateway.subscriptionQuery(
                    new WatchEventQuery(accountId),
                    ResponseTypes.instanceOf(AccountOperation.class),
                    ResponseTypes.instanceOf(AccountOperation.class)
            );
            return result.initialResult().thenMany(result.updates()).doFinally(signal -> result.close());
        });
    }
}
