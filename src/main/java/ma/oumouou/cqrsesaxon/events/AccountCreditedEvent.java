package ma.oumouou.cqrsesaxon.events;

public record AccountCreditedEvent(String accountId, double amount, String currency) {
}
