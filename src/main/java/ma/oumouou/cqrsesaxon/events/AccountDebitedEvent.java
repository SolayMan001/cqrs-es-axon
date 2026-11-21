package ma.oumouou.cqrsesaxon.events;

public record AccountDebitedEvent(String accountId, double amount, String currency) {
}
