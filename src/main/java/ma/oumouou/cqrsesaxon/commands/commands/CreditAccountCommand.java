package ma.oumouou.cqrsesaxon.commands.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CreditAccountCommand(@TargetAggregateIdentifier String id, double amount, String currency) {
}
