package ma.oumouou.cqrsesaxon.query.dtos;

import ma.oumouou.cqrsesaxon.query.entities.Account;

import java.util.List;

public record AccountListResponseDto(List<Account> accounts) {
}
