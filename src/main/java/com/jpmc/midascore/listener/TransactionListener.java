package com.jpmc.midascore.listener;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class TransactionListener {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionListener(UserRepository userRepository,
                               TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Value("${general.kafka-topic}")
    private String topic;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    @Transactional
    public void listen(Transaction transaction) {

        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());

        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
            UserRecord sender = senderOpt.get();
            UserRecord recipient = recipientOpt.get();

            if (sender.getBalance() >= transaction.getAmount()) {
                // Update balances
                sender.setBalance(sender.getBalance() - transaction.getAmount());
                recipient.setBalance(recipient.getBalance() + transaction.getAmount());

                userRepository.save(sender);
                userRepository.save(recipient);

                // Save transaction record
                TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount());
                transactionRecordRepository.save(record);

                System.out.println("Transaction recorded: " + record);

                // DEBUG: print Waldorf balance
                Optional<UserRecord> waldorfOpt = userRepository.findByUsername("waldorf");
                waldorfOpt.ifPresent(w -> System.out.println("Waldorf balance: " + (int) w.getBalance()));

            } else {
                System.out.println("Transaction discarded due to insufficient balance: " + transaction);
            }
        } else {
            System.out.println("Transaction discarded due to invalid sender/recipient: " + transaction);
        }
    }
}
