package ma.oumouou.cqrsesaxon.query.handlers;

import ma.oumouou.cqrsesaxon.query.dtos.AccountListResponseDto;
import ma.oumouou.cqrsesaxon.query.dtos.AccountStatementResponseDto;
import ma.oumouou.cqrsesaxon.query.entities.AccountOperation;
import ma.oumouou.cqrsesaxon.query.queries.GetAccountStatementQuery;
import ma.oumouou.cqrsesaxon.query.queries.GetAllAccountsQuery;
import ma.oumouou.cqrsesaxon.query.queries.WatchEventQuery;
import ma.oumouou.cqrsesaxon.query.repositories.AccountRepository;
import ma.oumouou.cqrsesaxon.query.repositories.OperationRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AccountQueryHandler {
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    public AccountQueryHandler(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @QueryHandler
    public AccountListResponseDto on(GetAllAccountsQuery query) {
        return new AccountListResponseDto(accountRepository.findAll());
    }

    @QueryHandler
    @Transactional(readOnly = true)
    public AccountStatementResponseDto on(GetAccountStatementQuery query) {
        return accountRepository.findById(query.accountId())
                .map(account -> new AccountStatementResponseDto(account,
                        operationRepository.findByAccountIdOrderByIdAsc(query.accountId())))
                .orElse(null);
    }

    @QueryHandler
    public AccountOperation on(WatchEventQuery query) {
        // The subscription starts with the next credit or debit.
        return null;
    }
}
