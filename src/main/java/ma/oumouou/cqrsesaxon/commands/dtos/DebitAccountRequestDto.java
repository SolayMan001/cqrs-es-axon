package ma.oumouou.cqrsesaxon.commands.dtos;

public record DebitAccountRequestDto(String accountId, double amount, String currency) {
}
