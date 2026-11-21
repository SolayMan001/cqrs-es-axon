package ma.oumouou.cqrsesaxon.query.handlers;

import ma.oumouou.cqrsesaxon.enums.OperationType;
import ma.oumouou.cqrsesaxon.events.AccountActivatedEvent;
import ma.oumouou.cqrsesaxon.events.AccountCreatedEvent;
import ma.oumouou.cqrsesaxon.events.AccountCreditedEvent;
import ma.oumouou.cqrsesaxon.events.AccountDebitedEvent;
import ma.oumouou.cqrsesaxon.events.AccountStatusUpdatedEvent;
import ma.oumouou.cqrsesaxon.query.entities.Account;
import ma.oumouou.cqrsesaxon.query.entities.AccountOperation;
import ma.oumouou.cqrsesaxon.query.queries.WatchEventQuery;
import ma.oumouou.cqrsesaxon.query.repositories.AccountRepository;
import ma.oumouou.cqrsesaxon.query.repositories.OperationRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
@ProcessingGroup("accounts")
public class AccountEventHandler {
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    public AccountEventHandler(AccountRepository accountRepository, OperationRepository operationRepository,
                               QueryUpdateEmitter queryUpdateEmitter) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    public void on(AccountCreatedEvent event, @Timestamp Instant timestamp) {
        accountRepository.save(new Account(event.getAccountId(), event.getInitialBalance(), event.getStatus(),
                event.getCurrency(), timestamp));
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        Account account = accountRepository.findById(event.accountId()).orElseThrow();
        account.setStatus(event.status());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountCreditedEvent event, @Timestamp Instant timestamp) {
        recordOperation(event.accountId(), event.amount(), event.currency(), OperationType.CREDIT, timestamp);
    }

    @EventHandler
    public void on(AccountDebitedEvent event, @Timestamp Instant timestamp) {
        recordOperation(event.accountId(), event.amount(), event.currency(), OperationType.DEBIT, timestamp);
    }

    @EventHandler
    public void on(AccountStatusUpdatedEvent event) {
        Account account = accountRepository.findById(event.accountId()).orElseThrow();
        account.setStatus(event.status());
        accountRepository.save(account);
    }

    private void recordOperation(String accountId, double amount, String currency, OperationType type, Instant timestamp) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        double change = type == OperationType.CREDIT ? amount : -amount;
        account.setBalance(BigDecimal.valueOf(account.getBalance()).add(BigDecimal.valueOf(change)).doubleValue());
        accountRepository.save(account);

        AccountOperation operation = operationRepository.save(new AccountOperation(timestamp, amount, type, currency, account));
        queryUpdateEmitter.emit(WatchEventQuery.class, query -> accountId.equals(query.accountId()), operation);
    }
}
