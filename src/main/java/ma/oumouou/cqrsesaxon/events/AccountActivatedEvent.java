package ma.oumouou.cqrsesaxon.events;

import ma.oumouou.cqrsesaxon.enums.AccountStatus;

public record AccountActivatedEvent(String accountId, AccountStatus status) {
}
