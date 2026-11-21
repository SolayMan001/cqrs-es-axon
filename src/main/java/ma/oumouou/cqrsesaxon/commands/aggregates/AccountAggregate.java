package ma.oumouou.cqrsesaxon.commands.aggregates;

import ma.oumouou.cqrsesaxon.commands.commands.AddAccountCommand;
import ma.oumouou.cqrsesaxon.commands.commands.CreditAccountCommand;
import ma.oumouou.cqrsesaxon.commands.commands.DebitAccountCommand;
import ma.oumouou.cqrsesaxon.commands.commands.UpdateAccountStatusCommand;
import ma.oumouou.cqrsesaxon.enums.AccountStatus;
import ma.oumouou.cqrsesaxon.events.AccountActivatedEvent;
import ma.oumouou.cqrsesaxon.events.AccountCreatedEvent;
import ma.oumouou.cqrsesaxon.events.AccountCreditedEvent;
import ma.oumouou.cqrsesaxon.events.AccountDebitedEvent;
import ma.oumouou.cqrsesaxon.events.AccountStatusUpdatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

@Aggregate
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private double balance;
    private String currency;
    private AccountStatus status;

    // Axon uses this constructor when replaying the account's events.
    protected AccountAggregate() {
    }

    @CommandHandler
    public AccountAggregate(AddAccountCommand command) {
        if (!Double.isFinite(command.getInitialBalance()) || command.getInitialBalance() <= 0) {
            throw new IllegalArgumentException("Initial balance must be greater than zero");
        }
        if (command.getCurrency() == null || command.getCurrency().isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        AggregateLifecycle.apply(new AccountCreatedEvent(
                command.getId(),
                command.getInitialBalance(),
                command.getCurrency(),
                AccountStatus.CREATED
        ));
        AggregateLifecycle.apply(new AccountActivatedEvent(command.getId(), AccountStatus.ACTIVATED));
    }

    @CommandHandler
    public void handle(CreditAccountCommand command) {
        validateOperation(command.amount(), command.currency());
        if (!Double.isFinite(BigDecimal.valueOf(balance).add(BigDecimal.valueOf(command.amount())).doubleValue())) {
            throw new IllegalArgumentException("The resulting balance is too large");
        }
        AggregateLifecycle.apply(new AccountCreditedEvent(accountId, command.amount(), currency));
    }

    @CommandHandler
    public void handle(DebitAccountCommand command) {
        validateOperation(command.amount(), command.currency());
        if (balance < command.amount()) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        AggregateLifecycle.apply(new AccountDebitedEvent(accountId, command.amount(), currency));
    }

    @CommandHandler
    public void handle(UpdateAccountStatusCommand command) {
        if (command.status() == null) {
            throw new IllegalArgumentException("Account status is required");
        }
        if (command.status() == status) {
            throw new IllegalArgumentException("The account is already " + status);
        }
        AggregateLifecycle.apply(new AccountStatusUpdatedEvent(accountId, command.status()));
    }

    private void validateOperation(double amount, String operationCurrency) {
        if (status != AccountStatus.ACTIVATED) {
            throw new IllegalArgumentException("The account must be activated");
        }
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (!currency.equals(operationCurrency)) {
            throw new IllegalArgumentException("Operation currency must match the account currency");
        }
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        accountId = event.getAccountId();
        balance = event.getInitialBalance();
        currency = event.getCurrency();
        status = event.getStatus();
    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent event) {
        status = event.status();
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        balance = BigDecimal.valueOf(balance).add(BigDecimal.valueOf(event.amount())).doubleValue();
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        balance = BigDecimal.valueOf(balance).subtract(BigDecimal.valueOf(event.amount())).doubleValue();
    }

    @EventSourcingHandler
    public void on(AccountStatusUpdatedEvent event) {
        status = event.status();
    }
}
