package com.jpmc.midascore.foundation;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class user {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private float balance;

    // For bi-directional mapping (optional)
    @OneToMany(mappedBy = "sender")
    private List<TransactionRecord> sentTransactions;

    @OneToMany(mappedBy = "recipient")
    private List<TransactionRecord> receivedTransactions;

    // Constructors
    public user() {}
    public user(String username, float balance) {
        this.username = username;
        this.balance = balance;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public float getBalance() { return balance; }
    public void setBalance(float balance) { this.balance = balance; }
}
