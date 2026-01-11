package com.smalaca.onlinebank.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @OnDelete(action = CASCADE)
    private Account source;

    @ManyToOne
    @OnDelete(action = CASCADE)
    private Account target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant occurredAt = Instant.now();

    protected Transaction() {}

    public Transaction(Account source, Account target, TransactionType type, BigDecimal amount) {
        this.source = source;
        this.target = target;
        this.type = type;
        this.amount = amount;
    }

    public UUID getId() { return id; }
    public Account getSource() { return source; }
    public Account getTarget() { return target; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public Instant getOccurredAt() { return occurredAt; }
}
