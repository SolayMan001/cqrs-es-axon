package ma.oumouou.cqrsesaxon.query.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.oumouou.cqrsesaxon.enums.AccountStatus;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    private String id;
    @Setter
    private double balance;
    @Setter
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private String currency;
    private Instant createdAt;

    public Account(String id, double balance, AccountStatus status, String currency, Instant createdAt) {
        this.id = id;
        this.balance = balance;
        this.status = status;
        this.currency = currency;
        this.createdAt = createdAt;
    }
}
