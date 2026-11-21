package ma.oumouou.cqrsesaxon.query.repositories;

import ma.oumouou.cqrsesaxon.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
