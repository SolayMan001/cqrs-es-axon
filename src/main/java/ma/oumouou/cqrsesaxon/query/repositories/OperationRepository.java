package ma.oumouou.cqrsesaxon.query.repositories;

import ma.oumouou.cqrsesaxon.query.entities.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationRepository extends JpaRepository<AccountOperation, Long> {
    List<AccountOperation> findByAccountIdOrderByIdAsc(String accountId);
}
