package ma.oumouou.cqrsesaxon.commands.commands;

import ma.oumouou.cqrsesaxon.enums.AccountStatus;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record UpdateAccountStatusCommand(@TargetAggregateIdentifier String id, AccountStatus status) {
}
