package ma.oumouou.cqrsesaxon.query.dtos;

import ma.oumouou.cqrsesaxon.query.entities.Account;
import ma.oumouou.cqrsesaxon.query.entities.AccountOperation;

import java.util.List;

public record AccountStatementResponseDto(Account account, List<AccountOperation> accountOperations) {
}
