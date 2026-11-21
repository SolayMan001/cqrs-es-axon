package ma.oumouou.cqrsesaxon.commands.dtos;

public record CreditAccountRequestDto(String accountId, double amount, String currency) {
}
