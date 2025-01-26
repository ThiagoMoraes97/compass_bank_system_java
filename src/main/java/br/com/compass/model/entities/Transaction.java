package br.com.compass.model.entities;

import java.time.LocalDateTime;

public class Transaction {

    private int id;
    private Account originAccount;
    private Account destinationAccount;
    private String transactionType;
    private Double amount;
    private LocalDateTime timestamp;
}
