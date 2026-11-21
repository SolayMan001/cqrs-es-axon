package ma.oumouou.cqrsesaxon.events;

import ma.oumouou.cqrsesaxon.enums.AccountStatus;

public record AccountStatusUpdatedEvent(String accountId, AccountStatus status) {
}
