package ma.oumouou.cqrsesaxon.commands.dtos;

import ma.oumouou.cqrsesaxon.enums.AccountStatus;

public record UpdateAccountStatusRequestDto(String accountId, AccountStatus accountStatus) {
}
