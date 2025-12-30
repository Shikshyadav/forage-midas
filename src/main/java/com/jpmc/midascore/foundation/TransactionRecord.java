package com.jpmc.midascore.foundation;

import com.jpmc.midascore.entity.UserRecord;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float amount;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    // Constructors
    public TransactionRecord() {}

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public float getAmount() { return amount; }
    public UserRecord getSender() { return sender; }
    public UserRecord getRecipient() { return recipient; }
}
